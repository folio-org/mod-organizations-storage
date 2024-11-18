package org.folio.service.audit;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.folio.kafka.KafkaConfig;
import org.folio.kafka.KafkaTopicNameHelper;
import org.folio.kafka.SimpleKafkaProducerManager;
import org.folio.kafka.services.KafkaProducerRecordBuilder;
import org.folio.rest.jaxrs.model.EventTopic;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationAuditEvent;
import org.folio.rest.tools.utils.TenantTool;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
public class AuditEventProducer {

  private final KafkaConfig kafkaConfig;

  /**
   * Sends event for organization change(Create, Edit) to kafka.
   * OrganizationId is used as partition key to send all events for particular organization to the same partition.
   *
   * @param organization      the event payload
   * @param eventAction  the event action
   * @param okapiHeaders the okapi headers
   * @return future with true if sending was success or failed future in another case
   */
  public Future<Void> sendOrganizationEvent(Organization organization, OrganizationAuditEvent.Action eventAction, Map<String, String> okapiHeaders) {
    var event = getAuditEvent(organization, eventAction);
    log.info("sendOrganizationEvent:: Sending event with id: {} and organizationId: {} to Kafka", event.getId(), organization.getId());
    return sendToKafka(EventTopic.ACQ_ORGANIZATION_CHANGED, event.getOrganizationId(), event, okapiHeaders)
      .onFailure(t -> log.warn("sendOrganizationEvent:: Failed to send event with id: {} and organizationId: {} to Kafka", event.getId(), organization.getId(), t));
  }

  private OrganizationAuditEvent getAuditEvent(Organization organization, OrganizationAuditEvent.Action eventAction) {
    return new OrganizationAuditEvent()
      .withId(UUID.randomUUID().toString())
      .withAction(eventAction)
      .withOrganizationId(organization.getId())
      .withEventDate(new Date())
      .withActionDate(organization.getMetadata().getUpdatedDate())
      .withUserId(organization.getMetadata().getUpdatedByUserId())
      .withOrganizationSnapshot(organization.withMetadata(null));
  }

  private Future<Void> sendToKafka(EventTopic eventTopic, String key, Object eventPayload, Map<String, String> okapiHeaders) {
    var tenantId = TenantTool.tenantId(okapiHeaders);
    var topicName = buildTopicName(kafkaConfig.getEnvId(), tenantId, eventTopic.value());
    KafkaProducerRecord<String, String> kafkaProducerRecord = new KafkaProducerRecordBuilder<String, Object>(tenantId)
      .key(key)
      .value(eventPayload)
      .topic(topicName)
      .propagateOkapiHeaders(okapiHeaders)
      .build();

    var producerManager = new SimpleKafkaProducerManager(Vertx.currentContext().owner(), kafkaConfig);
    KafkaProducer<String, String> producer = producerManager.createShared(topicName);
    return producer.send(kafkaProducerRecord)
      .onSuccess(s -> log.info("sendToKafka:: Event for {} with id '{}' has been sent to kafka topic '{}'", eventTopic, key, topicName))
      .onFailure(t -> log.error("Failed to send event for {} with id '{}' to kafka topic '{}'", eventTopic, key, topicName, t))
      .onComplete(reply -> producer.end(v -> producer.close()))
      .mapEmpty();
  }

  private String buildTopicName(String envId, String tenantId, String eventType) {
    return KafkaTopicNameHelper.formatTopicName(envId, KafkaTopicNameHelper.getDefaultNameSpace(), tenantId, eventType);
  }

}
