package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Interface;
import org.folio.rest.jaxrs.model.InterfaceCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageInterfaces;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

public class InterfacesAPI implements OrganizationsStorageInterfaces {
  private static final String INTERFACE_TABLE = "interfaces";

  private String idFieldName = "id";

  public InterfacesAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getOrganizationsStorageInterfaces(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      EntitiesMetadataHolder<Interface, InterfaceCollection> entitiesMetadataHolder = new EntitiesMetadataHolder<>(Interface.class, InterfaceCollection.class, GetOrganizationsStorageInterfacesResponse.class);
      QueryHolder cql = new QueryHolder(INTERFACE_TABLE, query, offset, limit);
      getEntitiesCollection(entitiesMetadataHolder, cql, asyncResultHandler, vertxContext, okapiHeaders);
    });
  }

  @Override
  @Validate
  public void postOrganizationsStorageInterfaces(String lang, org.folio.rest.jaxrs.model.Interface entity,
                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(INTERFACE_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageInterfacesResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageInterfacesById(String id, String lang, Map<String, String> okapiHeaders,
                                           Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(INTERFACE_TABLE, Interface.class, id, okapiHeaders,vertxContext, GetOrganizationsStorageInterfacesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageInterfacesById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(INTERFACE_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStorageInterfacesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageInterfacesById(String id, String lang, org.folio.rest.jaxrs.model.Interface entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(INTERFACE_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStorageInterfacesByIdResponse.class, asyncResultHandler);
  }
}
