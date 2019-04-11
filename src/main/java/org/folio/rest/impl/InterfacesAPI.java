package org.folio.rest.impl;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.RestVerticle;
import org.folio.rest.jaxrs.model.Interface;
import org.folio.rest.jaxrs.model.InterfaceCollection;
import org.folio.rest.jaxrs.resource.OrganizationStorageInterfaces;
import org.folio.rest.persist.Criteria.Limit;
import org.folio.rest.persist.Criteria.Offset;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.cql.CQLWrapper;
import org.folio.rest.tools.messages.MessageConsts;
import org.folio.rest.tools.messages.Messages;
import org.folio.rest.tools.utils.TenantTool;
import org.z3950.zing.cql.cql2pgjson.CQL2PgJSON;
import org.folio.rest.annotations.Validate;
import org.folio.rest.persist.PgUtil;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class InterfacesAPI implements OrganizationStorageInterfaces {
  private static final String INTERFACE_TABLE = "interface";

  private static final Logger log = LoggerFactory.getLogger(InterfacesAPI.class);
  private final Messages messages = Messages.getInstance();
  private String idFieldName = "id";

  public InterfacesAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getOrganizationStorageInterfaces(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      try {
        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );

        String[] fieldList = {"*"};
        CQL2PgJSON cql2PgJSON = new CQL2PgJSON(String.format("%s.jsonb", INTERFACE_TABLE));
        CQLWrapper cql = new CQLWrapper(cql2PgJSON, query)
          .setLimit(new Limit(limit))
          .setOffset(new Offset(offset));

        PostgresClient.getInstance(vertxContext.owner(), tenantId).get(INTERFACE_TABLE, Interface.class, fieldList, cql,
          true, false, reply -> {
            try {
              if(reply.succeeded()){
                InterfaceCollection collection = new InterfaceCollection();
                @SuppressWarnings("unchecked")
                List<Interface> results = reply.result().getResults();
                collection.setInterfaces(results);
                Integer totalRecords = reply.result().getResultInfo().getTotalRecords();
                collection.setTotalRecords(totalRecords);
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageInterfaces.GetOrganizationStorageInterfacesResponse
                  .respond200WithApplicationJson(collection)));
              }
              else{
                log.error(reply.cause().getMessage(), reply.cause());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageInterfaces.GetOrganizationStorageInterfacesResponse
                  .respond400WithTextPlain(reply.cause().getMessage())));
              }
            } catch (Exception e) {
              log.error(e.getMessage(), e);
              asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageInterfaces.GetOrganizationStorageInterfacesResponse
                .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
            }
          });
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        String message = messages.getMessage(lang, MessageConsts.InternalServerError);
        if(e.getCause() != null && e.getCause().getClass().getSimpleName().endsWith("CQLParseException")){
          message = " CQL parse error " + e.getLocalizedMessage();
        }
        asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageInterfaces.GetOrganizationStorageInterfacesResponse
          .respond500WithTextPlain(message)));
      }
    });
  }

  @Override
  @Validate
  public void postOrganizationStorageInterfaces(String lang, org.folio.rest.jaxrs.model.Interface entity,
                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(INTERFACE_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationStorageInterfacesResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationStorageInterfacesById(String id, String lang, Map<String, String> okapiHeaders,
                                           Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(INTERFACE_TABLE, Interface.class, id, okapiHeaders,vertxContext, GetOrganizationStorageInterfacesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationStorageInterfacesById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(INTERFACE_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationStorageInterfacesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationStorageInterfacesById(String id, String lang, org.folio.rest.jaxrs.model.Interface entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(INTERFACE_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationStorageInterfacesByIdResponse.class, asyncResultHandler);
  }
}
