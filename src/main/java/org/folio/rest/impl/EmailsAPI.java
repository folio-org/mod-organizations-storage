package org.folio.rest.impl;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.RestVerticle;
import org.folio.rest.jaxrs.model.Email;
import org.folio.rest.jaxrs.model.EmailCollection;
import org.folio.rest.jaxrs.resource.OrganizationStorageEmails;
import org.folio.rest.persist.Criteria.Limit;
import org.folio.rest.persist.Criteria.Offset;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.cql.CQLWrapper;
import org.folio.rest.tools.messages.MessageConsts;
import org.folio.rest.tools.messages.Messages;
import org.folio.rest.tools.utils.TenantTool;
import org.z3950.zing.cql.cql2pgjson.CQL2PgJSON;
import org.folio.rest.annotations.Validate;
import org.folio.rest.persist.PgUtil;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class EmailsAPI implements OrganizationStorageEmails {
  private static final String EMAIL_TABLE = "email";

  private static final Logger log = LoggerFactory.getLogger(EmailsAPI.class);
  private final Messages messages = Messages.getInstance();
  private String idFieldName = "id";

  public EmailsAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getOrganizationStorageEmails(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      try {
        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );

        String[] fieldList = {"*"};
        CQL2PgJSON cql2PgJSON = new CQL2PgJSON(String.format("%s.jsonb", EMAIL_TABLE));
        CQLWrapper cql = new CQLWrapper(cql2PgJSON, query)
          .setLimit(new Limit(limit))
          .setOffset(new Offset(offset));

        PostgresClient.getInstance(vertxContext.owner(), tenantId).get(EMAIL_TABLE, Email.class, fieldList, cql,
          true, false, reply -> {
            try {
              if(reply.succeeded()){
                EmailCollection collection = new EmailCollection();
                List<Email> results = reply.result().getResults();
                collection.setEmails(results);
                Integer totalRecords = reply.result().getResultInfo().getTotalRecords();
                collection.setTotalRecords(totalRecords);
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageEmails.GetOrganizationStorageEmailsResponse
                  .respond200WithApplicationJson(collection)));
              }
              else{
                log.error(reply.cause().getMessage(), reply.cause());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageEmails.GetOrganizationStorageEmailsResponse
                  .respond400WithTextPlain(reply.cause().getMessage())));
              }
            } catch (Exception e) {
              log.error(e.getMessage(), e);
              asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageEmails.GetOrganizationStorageEmailsResponse
                .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
            }
          });
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        String message = messages.getMessage(lang, MessageConsts.InternalServerError);
        if(e.getCause() != null && e.getCause().getClass().getSimpleName().endsWith("CQLParseException")){
          message = " CQL parse error " + e.getLocalizedMessage();
        }
        asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageEmails.GetOrganizationStorageEmailsResponse
          .respond500WithTextPlain(message)));
      }
    });
  }

  @Override
  @Validate
  public void postOrganizationStorageEmails(String lang, org.folio.rest.jaxrs.model.Email entity,
                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(EMAIL_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationStorageEmailsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationStorageEmailsById(String id, String lang, Map<String, String> okapiHeaders,
                                           Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(EMAIL_TABLE, Email.class, id, okapiHeaders,vertxContext, GetOrganizationStorageEmailsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationStorageEmailsById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(EMAIL_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationStorageEmailsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationStorageEmailsById(String id, String lang, org.folio.rest.jaxrs.model.Email entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(EMAIL_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationStorageEmailsByIdResponse.class, asyncResultHandler);
  }
}
