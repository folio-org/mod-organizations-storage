package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.PhoneNumber;
import org.folio.rest.jaxrs.model.PhoneNumberCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStoragePhoneNumbers;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

public class PhoneNumbersAPI implements OrganizationsStoragePhoneNumbers {
  private static final String PHONE_NUMBER_TABLE = "phone_numbers";

  private String idFieldName = "id";

  public PhoneNumbersAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getOrganizationsStoragePhoneNumbers(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      EntitiesMetadataHolder<PhoneNumber, PhoneNumberCollection> entitiesMetadataHolder = new EntitiesMetadataHolder<>(PhoneNumber.class, PhoneNumberCollection.class, GetOrganizationsStoragePhoneNumbersResponse.class);
      QueryHolder cql = new QueryHolder(PHONE_NUMBER_TABLE, query, offset, limit);
      getEntitiesCollection(entitiesMetadataHolder, cql, asyncResultHandler, vertxContext, okapiHeaders);
    });
  }

  @Override
  @Validate
  public void postOrganizationsStoragePhoneNumbers(String lang, org.folio.rest.jaxrs.model.PhoneNumber entity,
                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(PHONE_NUMBER_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStoragePhoneNumbersResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStoragePhoneNumbersById(String id, String lang, Map<String, String> okapiHeaders,
                                           Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(PHONE_NUMBER_TABLE, PhoneNumber.class, id, okapiHeaders,vertxContext, GetOrganizationsStoragePhoneNumbersByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStoragePhoneNumbersById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(PHONE_NUMBER_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStoragePhoneNumbersByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStoragePhoneNumbersById(String id, String lang, org.folio.rest.jaxrs.model.PhoneNumber entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(PHONE_NUMBER_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStoragePhoneNumbersByIdResponse.class, asyncResultHandler);
  }
}
