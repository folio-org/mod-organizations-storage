package org.folio.rest.impl;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Interface;
import org.folio.rest.jaxrs.model.InterfaceCollection;
import org.folio.rest.jaxrs.model.InterfaceCredential;
import org.folio.rest.jaxrs.resource.OrganizationsStorageInterfaces;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.QueryHolder;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class InterfacesAPI implements OrganizationsStorageInterfaces {
  private static final String INTERFACE_TABLE = "interfaces";
  private static final String INTERFACE_CREDENTIAL_TABLE = "interface_credentials";
  private static final String MISMATCH_ERROR_MESSAGE = "Interface credential id mismatch";

  @Override
  @Validate
  public void getOrganizationsStorageInterfaces(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
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
    PgUtil.getById(INTERFACE_TABLE, Interface.class, id, okapiHeaders, vertxContext, GetOrganizationsStorageInterfacesByIdResponse.class, asyncResultHandler);
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

  @Override
  @Validate
  public void postOrganizationsStorageInterfacesCredentialsById(String id, InterfaceCredential entity,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    if (!StringUtils.equals(entity.getId(), id) && StringUtils.isNotEmpty(entity.getId())) {
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostOrganizationsStorageInterfacesCredentialsByIdResponse.respond400WithTextPlain(MISMATCH_ERROR_MESSAGE)));
    } else {
      entity.setId(id);
      PgUtil.post(INTERFACE_CREDENTIAL_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageInterfacesCredentialsByIdResponse.class, asyncResultHandler);
    }
  }

  @Override
  @Validate
  public void getOrganizationsStorageInterfacesCredentialsById(String id, String lang, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(INTERFACE_CREDENTIAL_TABLE, InterfaceCredential.class, id, okapiHeaders, vertxContext, GetOrganizationsStorageInterfacesCredentialsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageInterfacesCredentialsById(String id, String lang, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(INTERFACE_CREDENTIAL_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStorageInterfacesCredentialsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageInterfacesCredentialsById(String id, String lang, InterfaceCredential entity,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    if (StringUtils.equals(id, entity.getId())) {
      PgUtil.put(INTERFACE_CREDENTIAL_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStorageInterfacesCredentialsByIdResponse.class, asyncResultHandler);
    } else {
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutOrganizationsStorageInterfacesCredentialsByIdResponse.respond400WithTextPlain(MISMATCH_ERROR_MESSAGE)));
    }
  }
}
