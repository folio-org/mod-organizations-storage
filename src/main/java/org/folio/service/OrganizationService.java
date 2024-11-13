package org.folio.service;

import static org.folio.rest.utils.ResponseUtils.buildBadRequestResponse;
import static org.folio.rest.utils.ResponseUtils.buildErrorResponse;
import static org.folio.rest.utils.ResponseUtils.buildNoContentResponse;
import static org.folio.rest.utils.ResponseUtils.buildResponseWithLocation;
import static org.folio.rest.utils.RestConstants.OKAPI_URL;
import static org.folio.rest.utils.RestConstants.ORGANIZATION_PREFIX;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.StringUtils;
import org.folio.dao.organization.OrganizationDAO;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationAuditEvent;
import org.folio.rest.persist.PostgresClient;
import org.folio.service.audit.AuditOutboxService;

import javax.ws.rs.core.Response;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class OrganizationService {

  private final OrganizationDAO organizationDAO;
  private final AuditOutboxService auditOutboxService;
  private final PostgresClient pgClient;

  public void createOrganization(Organization organization, Map<String, String> headers,
                                 Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    log.info("createOrganization:: Creating a new organization by id: {}", organization.getId());
    pgClient.withTrans(conn -> organizationDAO.createOrganization(organization, conn)
      .compose(id -> auditOutboxService.saveOrganizationOutboxLog(conn, organization, OrganizationAuditEvent.Action.CREATE, headers)))
      .onSuccess(s -> {
        log.info("createOrganization:: Successfully created a new organization by id: {}", organization.getId());
        auditOutboxService.processOutboxEventLogs(headers, vertxContext);
        var endpoint = ORGANIZATION_PREFIX.getValue() + "/" + organization.getId();
        asyncResultHandler.handle(buildResponseWithLocation(headers.get(OKAPI_URL.getValue()), endpoint, organization));
      })
      .onFailure(f -> {
        log.error("Error occurred while creating a new organization with id: {}", organization.getId(), f);
        asyncResultHandler.handle(buildErrorResponse(f));
      });
  }

  public void updateOrganization(String id, Organization organization, Map<String, String> headers,
                                 Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    log.info("updateOrganization:: Updating organization with id: {}", id);
    if (StringUtils.isBlank(id)) {
      asyncResultHandler.handle(buildBadRequestResponse("Organization id is required"));
    }
    pgClient.withTrans(conn -> organizationDAO.updateOrganization(id, organization, conn)
      .compose(organizationId -> auditOutboxService.saveOrganizationOutboxLog(conn, organization, OrganizationAuditEvent.Action.EDIT, headers)))
      .onSuccess(s -> {
        log.info("updateOrganization:: Successfully updated organization with id: {}", id);
        auditOutboxService.processOutboxEventLogs(headers, vertxContext);
        asyncResultHandler.handle(buildNoContentResponse());
      })
      .onFailure(f -> {
        log.error("Error occurred while updating organization with id: {}", id, f);
        asyncResultHandler.handle(buildErrorResponse(f));
      });
  }

  public void deleteOrganization(String id, Handler<AsyncResult<Response>> asyncResultHandler) {
    pgClient.withTrans(conn -> organizationDAO.deleteOrganization(id, conn))
      .onSuccess(rowSet -> {
        log.info("deleteOrganization:: Organization with id: '{}' and associated data were successfully deleted", id);
        asyncResultHandler.handle(buildNoContentResponse());
      }).onFailure(throwable -> {
        log.error("Failed to delete organization with id:  '{}' or associated data", id, throwable);
        asyncResultHandler.handle(buildErrorResponse(throwable));
      });
  }
}
