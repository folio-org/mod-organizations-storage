package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.BankingInformation;
import org.folio.rest.jaxrs.model.BankingInformationCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageBankingInformation;
import org.folio.rest.persist.PgUtil;

import javax.ws.rs.core.Response;
import java.util.Map;

public class BankingInformationAPI implements OrganizationsStorageBankingInformation {

  private static final String BANKING_INFORMATION = "banking_information";

  @Override
  @Validate
  public void getOrganizationsStorageBankingInformation(String query, String totalRecords, int offset, int limit,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.get(BANKING_INFORMATION, BankingInformation.class, BankingInformationCollection.class, query, offset, limit,
      okapiHeaders, vertxContext, OrganizationsStorageBankingInformation.GetOrganizationsStorageBankingInformationResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStorageBankingInformation(BankingInformation entity, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(BANKING_INFORMATION, entity, okapiHeaders, vertxContext, OrganizationsStorageBankingInformation.PostOrganizationsStorageBankingInformationResponse.class,
      asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageBankingInformationById(String id, BankingInformation entity,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(BANKING_INFORMATION, entity, id, okapiHeaders, vertxContext,
      OrganizationsStorageBankingInformation.PutOrganizationsStorageBankingInformationByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageBankingInformationById(String id, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(BANKING_INFORMATION, id, okapiHeaders, vertxContext,
      OrganizationsStorageBankingInformation.DeleteOrganizationsStorageBankingInformationByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageBankingInformationById(String id, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(BANKING_INFORMATION, BankingInformation.class, id, okapiHeaders, vertxContext,
      OrganizationsStorageBankingInformation.GetOrganizationsStorageBankingInformationByIdResponse.class, asyncResultHandler);
  }
}
