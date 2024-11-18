package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageOrganizations;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import org.folio.service.OrganizationService;
import org.folio.spring.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class OrganizationsAPI implements OrganizationsStorageOrganizations {

  private static final String ORGANIZATION_TABLE = "organizations";

  @Autowired
  private OrganizationService organizationService;

  public OrganizationsAPI() {
    SpringContextUtil.autowireDependencies(this, Vertx.currentContext());
  }

  @Override
  @Validate
  public void getOrganizationsStorageOrganizations(String query, String totalRecords, int offset, int limit,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.get(ORGANIZATION_TABLE, Organization.class, OrganizationCollection.class, query, offset, limit, okapiHeaders,
        vertxContext, GetOrganizationsStorageOrganizationsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStorageOrganizations(Organization entity, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    organizationService.createOrganization(entity, okapiHeaders, asyncResultHandler, vertxContext);
  }

  @Override
  @Validate
  public void getOrganizationsStorageOrganizationsById(String id, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(ORGANIZATION_TABLE, Organization.class, id, okapiHeaders, vertxContext,
        GetOrganizationsStorageOrganizationsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageOrganizationsById(String id, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    organizationService.deleteOrganization(id, okapiHeaders, asyncResultHandler, vertxContext);
  }

  @Override
  @Validate
  public void putOrganizationsStorageOrganizationsById(String id, Organization entity,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    organizationService.updateOrganization(id, entity, okapiHeaders, asyncResultHandler, vertxContext);
  }
}
