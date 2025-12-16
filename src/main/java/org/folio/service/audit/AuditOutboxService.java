package org.folio.service.audit;

import static org.folio.dao.DbUtils.getPgClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.folio.dao.audit.AuditOutboxEventLogDAO;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationAuditEvent;
import org.folio.rest.jaxrs.model.OutboxEventLog;
import org.folio.rest.jaxrs.model.OutboxEventLog.EntityType;
import org.folio.rest.persist.Conn;
import org.folio.rest.tools.utils.TenantTool;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class AuditOutboxService {

  private final AuditOutboxEventLogDAO outboxEventLogDAO;
  private final AuditEventProducer producer;

  /**
   * Reads outbox event logs from DB and send them to Kafka and delete from outbox table in a single transaction.
   *
   * @param okapiHeaders the okapi headers
   * @param vertxContext the vertx context
   * @return future with integer how many records have been processed
   */
  public Future<Integer> processOutboxEventLogs(Map<String, String> okapiHeaders, Context vertxContext) {
    var tenantId = TenantTool.tenantId(okapiHeaders);
    return getPgClient(vertxContext, okapiHeaders)
      .withTrans(conn -> outboxEventLogDAO.getEventLogs(conn, tenantId)
        .compose(logs -> {
          if (CollectionUtils.isEmpty(logs)) {
            log.info("processOutboxEventLogs: No logs found in outbox table");
            return Future.succeededFuture(0);
          }
          log.info("processOutboxEventLogs: {} logs found in outbox table, sending to kafka", logs.size());
          return Future.join(sendEventLogsToKafka(logs, okapiHeaders))
            .map(logs.stream().map(OutboxEventLog::getEventId).toList())
            .compose(eventIds -> outboxEventLogDAO.deleteEventLogs(conn, eventIds, tenantId))
            .onSuccess(count -> log.info("processOutboxEventLogs:: {} logs have been deleted from outbox table", count))
            .onFailure(ex -> log.error("Logs deletion failed", ex));
        })
        .onSuccess(count -> log.info("processOutboxEventLogs:: Successfully processed outbox event logs: {}", count))
        .onFailure(ex -> log.error("Failed to process outbox event logs", ex)));
  }

  private List<Future<Void>> sendEventLogsToKafka(List<OutboxEventLog> eventLogs, Map<String, String> okapiHeaders) {
    return eventLogs.stream().map(eventLog ->
      switch (eventLog.getEntityType()) {
        case ORGANIZATION -> {
          var organization = Json.decodeValue(eventLog.getPayload(), Organization.class);
          var action = OrganizationAuditEvent.Action.fromValue(eventLog.getAction());
          yield producer.sendOrganizationEvent(organization, action, okapiHeaders);
        }
      }).toList();
  }

  /**
   * Saves organization outbox log.
   *
   * @param conn         connection in transaction
   * @param entity       the organization
   * @param action       the event action
   * @param okapiHeaders okapi headers
   * @return future with saved outbox log id in the same transaction
   */
  public Future<Void> saveOrganizationOutboxLog(Conn conn, Organization entity, OrganizationAuditEvent.Action action, Map<String, String> okapiHeaders) {
    return saveOutboxLog(conn, okapiHeaders, action.value(), EntityType.ORGANIZATION, entity.getId(), entity);
  }

  private Future<Void> saveOutboxLog(Conn conn, Map<String, String> okapiHeaders, String action, EntityType entityType, String entityId, Object entity) {
    log.debug("saveOutboxLog:: Saving outbox log for {} with id: {}", entityType, entityId);
    var eventLog = new OutboxEventLog()
      .withEventId(UUID.randomUUID().toString())
      .withAction(action)
      .withEntityType(entityType)
      .withPayload(Json.encode(entity));
    return outboxEventLogDAO.saveEventLog(conn, eventLog, TenantTool.tenantId(okapiHeaders))
      .onSuccess(reply -> log.info("saveOutboxLog:: Outbox log has been saved for {} with id: {}", entityType, entityId))
      .onFailure(e -> log.warn("saveOutboxLog:: Could not save outbox audit log for {} with id: {}", entityType, entityId, e));
  }

}
