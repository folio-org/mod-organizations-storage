package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationCollection;
import org.folio.rest.jaxrs.resource.OrganizationStorageOrganizations;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

public class OrganizationsAPI implements OrganizationStorageOrganizations {
  private static final String ORGANIZATION_TABLE = "organization";

  private String idFieldName = "id";

  public OrganizationsAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }

  @Override
  public void getOrganizationStorageOrganizations(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      EntitiesMetadataHolder<Organization, OrganizationCollection> entitiesMetadataHolder = new EntitiesMetadataHolder<>(Organization.class, OrganizationCollection.class, GetOrganizationStorageOrganizationsResponse.class);
      QueryHolder cql = new QueryHolder(ORGANIZATION_TABLE, query, offset, limit, lang);
      getEntitiesCollection(entitiesMetadataHolder, cql, asyncResultHandler, vertxContext, okapiHeaders);
    });
  }

  @Override
  @Validate
  public void postOrganizationStorageOrganizations(String lang, Organization entity,
                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(ORGANIZATION_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationStorageOrganizationsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationStorageOrganizationsById(String id, String lang, Map<String, String> okapiHeaders,
                                           Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(ORGANIZATION_TABLE, Organization.class, id, okapiHeaders,vertxContext, GetOrganizationStorageOrganizationsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationStorageOrganizationsById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(ORGANIZATION_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationStorageOrganizationsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationStorageOrganizationsById(String id, String lang, Organization entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(ORGANIZATION_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationStorageOrganizationsByIdResponse.class, asyncResultHandler);
  }
}
