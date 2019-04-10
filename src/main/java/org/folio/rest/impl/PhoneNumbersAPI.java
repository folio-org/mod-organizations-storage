package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.PhoneNumber;
import org.folio.rest.jaxrs.model.PhoneNumberCollection;
import org.folio.rest.jaxrs.resource.OrganizationStoragePhoneNumbers;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

public class PhoneNumbersAPI implements OrganizationStoragePhoneNumbers {
  private static final String PHONE_NUMBER_TABLE = "phone_number";

  private String idFieldName = "id";

  public PhoneNumbersAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getOrganizationStoragePhoneNumbers(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      EntitiesMetadataHolder<PhoneNumber, PhoneNumberCollection> entitiesMetadataHolder = new EntitiesMetadataHolder<>(PhoneNumber.class, PhoneNumberCollection.class, GetOrganizationStoragePhoneNumbersResponse.class);
      QueryHolder cql = new QueryHolder(PHONE_NUMBER_TABLE, query, offset, limit, lang);
      getEntitiesCollection(entitiesMetadataHolder, cql, asyncResultHandler, vertxContext, okapiHeaders);
    });
  }

  @Override
  @Validate
  public void postOrganizationStoragePhoneNumbers(String lang, org.folio.rest.jaxrs.model.PhoneNumber entity,
                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(PHONE_NUMBER_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationStoragePhoneNumbersResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationStoragePhoneNumbersById(String id, String lang, Map<String, String> okapiHeaders,
                                           Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(PHONE_NUMBER_TABLE, PhoneNumber.class, id, okapiHeaders,vertxContext, GetOrganizationStoragePhoneNumbersByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationStoragePhoneNumbersById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(PHONE_NUMBER_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationStoragePhoneNumbersByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationStoragePhoneNumbersById(String id, String lang, org.folio.rest.jaxrs.model.PhoneNumber entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(PHONE_NUMBER_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationStoragePhoneNumbersByIdResponse.class, asyncResultHandler);
  }
}
