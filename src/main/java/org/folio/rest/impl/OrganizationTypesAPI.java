package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.OrganizationType;
import org.folio.rest.jaxrs.model.OrganizationTypeCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageOrganizationTypes;
import org.folio.rest.persist.PgUtil;

public class OrganizationTypesAPI implements OrganizationsStorageOrganizationTypes {
  private static final String ORGANIZATION_TYPE_TABLE = "organization_types";

  @Override
  @Validate
  public void getOrganizationsStorageOrganizationTypes(
      String query,
      String totalRecords,
      int offset,
      int limit,
      Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) {
    PgUtil.get(
        ORGANIZATION_TYPE_TABLE,
        OrganizationType.class,
        OrganizationTypeCollection.class,
        query,
        offset,
        limit,
        okapiHeaders,
        vertxContext,
        GetOrganizationsStorageOrganizationTypesResponse.class,
        asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStorageOrganizationTypes(
      OrganizationType entity,
      Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) {
    PgUtil.post(
        ORGANIZATION_TYPE_TABLE,
        entity,
        okapiHeaders,
        vertxContext,
        PostOrganizationsStorageOrganizationTypesResponse.class,
        asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageOrganizationTypesById(
      String id,
      Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) {
    PgUtil.getById(
        ORGANIZATION_TYPE_TABLE,
        OrganizationType.class,
        id,
        okapiHeaders,
        vertxContext,
        GetOrganizationsStorageOrganizationTypesByIdResponse.class,
        asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageOrganizationTypesById(
      String id,
      Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) {
    PgUtil.deleteById(
        ORGANIZATION_TYPE_TABLE,
        id,
        okapiHeaders,
        vertxContext,
        DeleteOrganizationsStorageOrganizationTypesByIdResponse.class,
        asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageOrganizationTypesById(
      String id,
      OrganizationType entity,
      Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) {
    PgUtil.put(
        ORGANIZATION_TYPE_TABLE,
        entity,
        id,
        okapiHeaders,
        vertxContext,
        PutOrganizationsStorageOrganizationTypesByIdResponse.class,
        asyncResultHandler);
  }
}
