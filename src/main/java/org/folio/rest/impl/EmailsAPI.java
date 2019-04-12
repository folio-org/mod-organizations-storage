package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Email;
import org.folio.rest.jaxrs.model.EmailCollection;
import org.folio.rest.jaxrs.resource.OrganizationStorageEmails;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

public class EmailsAPI implements OrganizationStorageEmails {
  private static final String EMAIL_TABLE = "emails";

  private String idFieldName = "id";

  public EmailsAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getOrganizationStorageEmails(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      EntitiesMetadataHolder<Email, EmailCollection> entitiesMetadataHolder = new EntitiesMetadataHolder<>(Email.class, EmailCollection.class, GetOrganizationStorageEmailsResponse.class);
      QueryHolder cql = new QueryHolder(EMAIL_TABLE, query, offset, limit);
      getEntitiesCollection(entitiesMetadataHolder, cql, asyncResultHandler, vertxContext, okapiHeaders);
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
