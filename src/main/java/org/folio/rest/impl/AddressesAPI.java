package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Address;
import org.folio.rest.jaxrs.model.AddressCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageAddresses;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class AddressesAPI implements OrganizationsStorageAddresses {
  private static final String ADDRESS_TABLE = "addresses";

  @Override
  @Validate
  public void getOrganizationsStorageAddresses(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.get(ADDRESS_TABLE, Address.class, AddressCollection.class, query, offset, limit, okapiHeaders, vertxContext,
        GetOrganizationsStorageAddressesResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStorageAddresses(String lang, org.folio.rest.jaxrs.model.Address entity,
                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(ADDRESS_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageAddressesResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageAddressesById(String id, String lang, Map<String, String> okapiHeaders,
                                           Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(ADDRESS_TABLE, Address.class, id, okapiHeaders,vertxContext, GetOrganizationsStorageAddressesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageAddressesById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(ADDRESS_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStorageAddressesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageAddressesById(String id, String lang, org.folio.rest.jaxrs.model.Address entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(ADDRESS_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStorageAddressesByIdResponse.class, asyncResultHandler);
  }
}
