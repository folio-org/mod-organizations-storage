package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.rest.RestVerticle;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Interface;
import org.folio.rest.jaxrs.model.InterfaceCollection;
import org.folio.rest.jaxrs.model.InterfaceCredential;
import org.folio.rest.jaxrs.resource.OrganizationsStorageInterfaces;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.cql.CQLWrapper;
import org.folio.rest.tools.utils.TenantTool;
import org.folio.service.InterfaceService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class InterfacesAPI implements OrganizationsStorageInterfaces {

  private static final String INTERFACE_TABLE = "interfaces";
  private static final String INTERFACE_CREDENTIAL_TABLE = "interface_credentials";
  private static final String MISMATCH_ERROR_MESSAGE = "Interface credential id mismatch";
  private final Logger logger = LogManager.getLogger(InterfacesAPI.class);

  private final InterfaceService interfaceService;

  public InterfacesAPI(Vertx vertx, String tenantId) {
    interfaceService = new InterfaceService(PostgresClient.getInstance(vertx, tenantId));
  }

  @Override
  @Validate
  public void getOrganizationsStorageInterfaces(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.get(INTERFACE_TABLE, Interface.class, InterfaceCollection.class, query, offset, limit, okapiHeaders, vertxContext,
        GetOrganizationsStorageInterfacesResponse.class, asyncResultHandler);
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
    interfaceService.deleteOrganizationsInterfaceById(id, vertxContext,  asyncResultHandler);
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
    if (StringUtils.equals(entity.getInterfaceId(), id) ) {
      PgUtil.post(INTERFACE_CREDENTIAL_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageInterfacesCredentialsByIdResponse.class, asyncResultHandler);
    } else {
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostOrganizationsStorageInterfacesCredentialsByIdResponse.respond400WithTextPlain(MISMATCH_ERROR_MESSAGE)));
    }
  }

  @Override
  @Validate
  public void getOrganizationsStorageInterfacesCredentialsById(String id, String lang, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      try {
        String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT));
        PostgresClient pgClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
        CQLWrapper cqlWrapper = getCqlWrapperByInterfaceId(id);

        pgClient.get(INTERFACE_CREDENTIAL_TABLE, InterfaceCredential.class, cqlWrapper,false, reply -> {
            try {
              if (reply.succeeded()) {
                if (reply.result().getResults().isEmpty()) {
                  asyncResultHandler.handle(Future.succeededFuture(GetOrganizationsStorageInterfacesCredentialsByIdResponse.respond404WithTextPlain(Response.Status.NOT_FOUND.getReasonPhrase())));
                } else {
                  InterfaceCredential response = reply.result().getResults().get(0);
                  asyncResultHandler.handle(Future.succeededFuture(GetOrganizationsStorageInterfacesCredentialsByIdResponse.respond200WithApplicationJson(response)));
                }
              } else {
                asyncResultHandler.handle(Future.succeededFuture(GetOrganizationsStorageInterfacesCredentialsByIdResponse.respond500WithTextPlain(reply.cause().getMessage())));
              }
            } catch (Exception e) {
              logger.error(e.getMessage(), e);
              asyncResultHandler.handle(Future.succeededFuture(GetOrganizationsStorageInterfacesCredentialsByIdResponse.respond500WithTextPlain(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase())));
            }
          });
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        asyncResultHandler.handle(Future.succeededFuture(GetOrganizationsStorageInterfacesCredentialsByIdResponse.respond500WithTextPlain(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase())));
      }
    });
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageInterfacesCredentialsById(String id, String lang, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      try {
        String tenantId = TenantTool.calculateTenantId(okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT));
        PostgresClient pgClient = PostgresClient.getInstance(vertxContext.owner(), tenantId);
        CQLWrapper cqlWrapper = getCqlWrapperByInterfaceId(id);

        pgClient.delete(INTERFACE_CREDENTIAL_TABLE, cqlWrapper, reply -> {
            try {
              if (reply.succeeded()) {
                if (reply.result().rowCount() == 0) {
                  asyncResultHandler.handle(Future.succeededFuture(DeleteOrganizationsStorageInterfacesCredentialsByIdResponse.respond404WithTextPlain(Response.Status.NOT_FOUND.getReasonPhrase())));
                } else {
                  asyncResultHandler.handle(Future.succeededFuture(DeleteOrganizationsStorageInterfacesCredentialsByIdResponse.respond204()));
                }
              } else {
                asyncResultHandler.handle(Future.succeededFuture(DeleteOrganizationsStorageInterfacesCredentialsByIdResponse.respond500WithTextPlain(reply.cause().getMessage())));
              }
            } catch (Exception e) {
              logger.error(e.getMessage(), e);
              asyncResultHandler.handle(Future.succeededFuture(DeleteOrganizationsStorageInterfacesCredentialsByIdResponse.respond500WithTextPlain(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase())));
            }
          });
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        asyncResultHandler.handle(Future.succeededFuture(DeleteOrganizationsStorageInterfacesCredentialsByIdResponse.respond500WithTextPlain(Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase())));
      }
    });
  }

  @Override
  @Validate
  public void putOrganizationsStorageInterfacesCredentialsById(String id, String lang, InterfaceCredential entity,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    if (StringUtils.equals(id, entity.getInterfaceId())) {
      PgUtil.put(INTERFACE_CREDENTIAL_TABLE, entity, entity.getId(), okapiHeaders, vertxContext, PutOrganizationsStorageInterfacesCredentialsByIdResponse.class, asyncResultHandler);
    } else {
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutOrganizationsStorageInterfacesCredentialsByIdResponse.respond400WithTextPlain(MISMATCH_ERROR_MESSAGE)));
    }
  }

  private CQLWrapper getCqlWrapperByInterfaceId(String interfaceId) {
    Criteria criteria = new Criteria();
    criteria.addField("'interfaceId'");
    criteria.setOperation("=");
    criteria.setVal(interfaceId);
    return new CQLWrapper(new Criterion(criteria));
  }
}
