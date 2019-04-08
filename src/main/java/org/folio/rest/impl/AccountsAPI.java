package org.folio.rest.impl;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.RestVerticle;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Account;
import org.folio.rest.jaxrs.model.AccountCollection;

import org.folio.rest.jaxrs.resource.OrganizationStorageAccounts;
import org.folio.rest.persist.Criteria.Limit;
import org.folio.rest.persist.Criteria.Offset;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.cql.CQLWrapper;
import org.folio.rest.tools.messages.MessageConsts;
import org.folio.rest.tools.messages.Messages;
import org.folio.rest.tools.utils.TenantTool;
import org.z3950.zing.cql.cql2pgjson.CQL2PgJSON;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public class AccountsAPI implements OrganizationStorageAccounts {
  private static final String ACCOUNT_TABLE = "account";

  private static final Logger log = LoggerFactory.getLogger(AccountsAPI.class);
  private final Messages messages = Messages.getInstance();
  private String idFieldName = "id";

  public AccountsAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  @Validate
  public void getOrganizationStorageAccounts(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      try {
        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );

        String[] fieldList = {"*"};
        CQL2PgJSON cql2PgJSON = new CQL2PgJSON(String.format("%s.jsonb", ACCOUNT_TABLE));
        CQLWrapper cql = new CQLWrapper(cql2PgJSON, query)
          .setLimit(new Limit(limit))
          .setOffset(new Offset(offset));

        PostgresClient.getInstance(vertxContext.owner(), tenantId).get(ACCOUNT_TABLE, org.folio.rest.jaxrs.model.Account.class, fieldList, cql,
          true, false, reply -> {
            try {
              if(reply.succeeded()){
                AccountCollection collection = new AccountCollection();
                @SuppressWarnings("unchecked")
                List<org.folio.rest.jaxrs.model.Account> results = reply.result().getResults();
                collection.setAccounts(results);
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
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageAccounts.GetOrganizationStorageAccountsResponse
                  .respond200WithApplicationJson(collection)));
              }
              else{
                log.error(reply.cause().getMessage(), reply.cause());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageAccounts.GetOrganizationStorageAccountsResponse
                  .respond400WithTextPlain(reply.cause().getMessage())));
              }
            } catch (Exception e) {
              log.error(e.getMessage(), e);
              asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageAccounts.GetOrganizationStorageAccountsResponse
                .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
            }
          });
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        String message = messages.getMessage(lang, MessageConsts.InternalServerError);
        if(e.getCause() != null && e.getCause().getClass().getSimpleName().endsWith("CQLParseException")){
          message = " CQL parse error " + e.getLocalizedMessage();
        }
        asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(OrganizationStorageAccounts.GetOrganizationStorageAccountsResponse
          .respond500WithTextPlain(message)));
      }
    });
  }

  @Override
  @Validate
  public void postOrganizationStorageAccounts(String lang, org.folio.rest.jaxrs.model.Account entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(ACCOUNT_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationStorageAccountsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationStorageAccountsById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(ACCOUNT_TABLE, Account.class, id, okapiHeaders,vertxContext, GetOrganizationStorageAccountsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationStorageAccountsById(String id, String lang, Map<String, String> okapiHeaders,
                                                 Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(ACCOUNT_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationStorageAccountsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationStorageAccountsById(String id, String lang, org.folio.rest.jaxrs.model.Account entity,
                                              Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(ACCOUNT_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationStorageAccountsByIdResponse.class, asyncResultHandler);
  }
}
