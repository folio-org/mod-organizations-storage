package org.folio.rest.impl;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.RestVerticle;
import org.folio.rest.jaxrs.model.Alias;
import org.folio.rest.jaxrs.model.AliasCollection;
import org.folio.rest.jaxrs.resource.OrganizationStorageAliases;
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

public class AliasesAPI implements OrganizationStorageAliases {
  private static final String ALIAS_TABLE = "alias";

  private static final Logger log = LoggerFactory.getLogger(AliasesAPI.class);
  private final Messages messages = Messages.getInstance();
  private String idFieldName = "id";

  public AliasesAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getOrganizationStorageAliases(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      try {
        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );

        String[] fieldList = {"*"};
        CQL2PgJSON cql2PgJSON = new CQL2PgJSON(String.format("%s.jsonb", ALIAS_TABLE));
        CQLWrapper cql = new CQLWrapper(cql2PgJSON, query)
          .setLimit(new Limit(limit))
          .setOffset(new Offset(offset));

        PostgresClient.getInstance(vertxContext.owner(), tenantId).get(ALIAS_TABLE, Alias.class, fieldList, cql,
          true, false, reply -> {
            try {
              if(reply.succeeded()){
                AliasCollection collection = new AliasCollection();
                @SuppressWarnings("unchecked")
                List<Alias> results = reply.result().getResults();
                collection.setAliases(results);
                Integer totalRecords = reply.result().getResultInfo().getTotalRecords();
                collection.setTotalRecords(totalRecords);
                Integer first = 0;
                Integer last = 0;
                if (!results.isEmpty()) {
                  first = offset + 1;
                  last = offset + results.size();
                }
                collection.setFirst(first);
                collection.setLast(last);
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageAliases.GetOrganizationStorageAliasesResponse
                  .respond200WithApplicationJson(collection)));
              }
              else{
                log.error(reply.cause().getMessage(), reply.cause());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageAliases.GetOrganizationStorageAliasesResponse
                  .respond400WithTextPlain(reply.cause().getMessage())));
              }
            } catch (Exception e) {
              log.error(e.getMessage(), e);
              asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageAliases.GetOrganizationStorageAliasesResponse
                .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
            }
          });
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        String message = messages.getMessage(lang, MessageConsts.InternalServerError);
        if(e.getCause() != null && e.getCause().getClass().getSimpleName().endsWith("CQLParseException")){
          message = " CQL parse error " + e.getLocalizedMessage();
        }
        asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageAliases.GetOrganizationStorageAliasesResponse
          .respond500WithTextPlain(message)));
      }
    });
  }

  @Override
  @Validate
  public void postOrganizationStorageAliases(String lang, org.folio.rest.jaxrs.model.Alias entity,
                                          Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(ALIAS_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationStorageAliasesResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationStorageAliasesById(String id, String lang, Map<String, String> okapiHeaders,
                                             Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(ALIAS_TABLE, Alias.class, id, okapiHeaders,vertxContext, GetOrganizationStorageAliasesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationStorageAliasesById(String id, String lang, Map<String, String> okapiHeaders,
                                                Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(ALIAS_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationStorageAliasesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationStorageAliasesById(String id, String lang, org.folio.rest.jaxrs.model.Alias entity,
                                             Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(ALIAS_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationStorageAliasesByIdResponse.class, asyncResultHandler);
  }
}
