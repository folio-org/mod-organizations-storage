package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageOrganizations;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class OrganizationsAPI implements OrganizationsStorageOrganizations {
  private static final String ORGANIZATION_TABLE = "organizations";

  @Override
  @Validate
  public void getOrganizationsStorageOrganizations(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.get(ORGANIZATION_TABLE, Organization.class, OrganizationCollection.class, query, offset, limit, okapiHeaders,
        vertxContext, GetOrganizationsStorageOrganizationsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStorageOrganizations(String lang, Organization entity,
                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(ORGANIZATION_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageOrganizationsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageOrganizationsById(String id, String lang, Map<String, String> okapiHeaders,
                                           Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(ORGANIZATION_TABLE, Organization.class, id, okapiHeaders,vertxContext, GetOrganizationsStorageOrganizationsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageOrganizationsById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(ORGANIZATION_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStorageOrganizationsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageOrganizationsById(String id, String lang, Organization entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(ORGANIZATION_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStorageOrganizationsByIdResponse.class, asyncResultHandler);
  }
}
