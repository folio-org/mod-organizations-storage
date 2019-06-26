package org.folio.rest.impl;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.folio.rest.RestVerticle;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Interface;
import org.folio.rest.jaxrs.model.InterfaceCollection;
import org.folio.rest.jaxrs.model.InterfaceCredential;
import org.folio.rest.jaxrs.resource.OrganizationsStorageInterfaces;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.tools.utils.TenantTool;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class InterfacesAPI implements OrganizationsStorageInterfaces {
  private static final String INTERFACE_TABLE = "interfaces";
  private static final String INTERFACE_CREDENTIAL_TABLE = "interface_credentials";
  private static final String MISMATCH_ERROR_MESSAGE = "Interface credential id mismatch";
  private final Logger logger = LoggerFactory.getLogger(InterfacesAPI.class);

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
        Criterion criterion = getCriterionByInterfaceId(id);

        pgClient.get(INTERFACE_CREDENTIAL_TABLE, InterfaceCredential.class, criterion,false, reply -> {
            try {
              if (reply.succeeded()) {
                if (reply.result().getResults().isEmpty()) {
                  asyncResultHandler.handle(Future.succeededFuture(GetOrganizationsStorageInterfacesCredentialsByIdResponse.respond404WithTextPlain(Response.Status.NOT_FOUND.getReasonPhrase())));
                } else {
                  InterfaceCredential response = reply.result().getResults().get(0);
                  asyncResultHandler.handle(Future.succeededFuture(GetOrganizationsStorageInterfacesCredentialsByIdResponse.respond200WithApplicationJson(response)));
                }
              } else {
                logger.error(reply.cause().getMessage(), reply.cause());
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
        Criterion criterion = getCriterionByInterfaceId(id);

        pgClient.delete(INTERFACE_CREDENTIAL_TABLE, criterion, reply -> {
            try {
              if (reply.succeeded()) {
                if (reply.result().getUpdated() == 0) {
                  asyncResultHandler.handle(Future.succeededFuture(DeleteOrganizationsStorageInterfacesCredentialsByIdResponse.respond404WithTextPlain(Response.Status.NOT_FOUND.getReasonPhrase())));
                } else {
                  asyncResultHandler.handle(Future.succeededFuture(DeleteOrganizationsStorageInterfacesCredentialsByIdResponse.respond204()));
                }
              } else {
                logger.error(reply.cause().getMessage(), reply.cause());
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

  private Criterion getCriterionByInterfaceId(String interfaceId) {
    Criteria criteria = new Criteria();
    criteria.addField("'interfaceId'");
    criteria.setOperation("=");
    criteria.setVal(interfaceId);
    return new Criterion(criteria);
  }
}
