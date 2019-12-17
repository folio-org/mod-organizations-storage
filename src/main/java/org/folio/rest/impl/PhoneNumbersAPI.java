package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.PhoneNumber;
import org.folio.rest.jaxrs.model.PhoneNumberCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStoragePhoneNumbers;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class PhoneNumbersAPI implements OrganizationsStoragePhoneNumbers {
  private static final String PHONE_NUMBER_TABLE = "phone_numbers";

  @Override
  @Validate
  public void getOrganizationsStoragePhoneNumbers(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.get(PHONE_NUMBER_TABLE, PhoneNumber.class, PhoneNumberCollection.class, query, offset, limit, okapiHeaders, vertxContext,
        GetOrganizationsStoragePhoneNumbersResponse.class, asyncResultHandler);
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
