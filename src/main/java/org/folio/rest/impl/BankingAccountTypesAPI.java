package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.BankingAccountType;
import org.folio.rest.jaxrs.model.BankingAccountTypeCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageBankingAccountTypes;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class BankingAccountTypesAPI implements OrganizationsStorageBankingAccountTypes {

  private static final String BANKING_ACCOUNT_TYPE_TABLE = "banking_account_types";

  @Override
  @Validate
  public void getOrganizationsStorageBankingAccountTypes(String query,
                                                         String totalRecords,
                                                         int offset,
                                                         int limit,
                                                         Map<String, String> okapiHeaders,
                                                         Handler<AsyncResult<Response>> asyncResultHandler,
                                                         Context vertxContext) {
    PgUtil.get(BANKING_ACCOUNT_TYPE_TABLE, BankingAccountType.class, BankingAccountTypeCollection.class, query, offset, limit,
      okapiHeaders, vertxContext, GetOrganizationsStorageBankingAccountTypesResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStorageBankingAccountTypes(BankingAccountType entity,
                                                          Map<String, String> okapiHeaders,
                                                          Handler<AsyncResult<Response>> asyncResultHandler,
                                                          Context vertxContext) {
    PgUtil.post(BANKING_ACCOUNT_TYPE_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageBankingAccountTypesResponse.class,
      asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageBankingAccountTypesById(String id,
                                                             Map<String, String> okapiHeaders,
                                                             Handler<AsyncResult<Response>> asyncResultHandler,
                                                             Context vertxContext) {
    PgUtil.getById(BANKING_ACCOUNT_TYPE_TABLE, BankingAccountType.class, id, okapiHeaders, vertxContext,
      GetOrganizationsStorageBankingAccountTypesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageBankingAccountTypesById(String id,
                                                                Map<String, String> okapiHeaders,
                                                                Handler<AsyncResult<Response>> asyncResultHandler,
                                                                Context vertxContext) {
    PgUtil.deleteById(BANKING_ACCOUNT_TYPE_TABLE, id, okapiHeaders, vertxContext,
      DeleteOrganizationsStorageBankingAccountTypesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageBankingAccountTypesById(String id,
                                                             BankingAccountType entity,
                                                             Map<String, String> okapiHeaders,
                                                             Handler<AsyncResult<Response>> asyncResultHandler,
                                                             Context vertxContext) {
    PgUtil.put(BANKING_ACCOUNT_TYPE_TABLE, entity, id, okapiHeaders, vertxContext,
      PutOrganizationsStorageBankingAccountTypesByIdResponse.class, asyncResultHandler);
  }

}
