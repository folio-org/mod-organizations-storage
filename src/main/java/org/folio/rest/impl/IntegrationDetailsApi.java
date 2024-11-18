package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import org.folio.rest.jaxrs.model.IntegrationDetail;
import org.folio.rest.jaxrs.model.IntegrationDetailCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageIntegrationDetails;
import org.folio.rest.persist.PgUtil;

import javax.ws.rs.core.Response;
import java.util.Map;

public class IntegrationDetailsApi implements OrganizationsStorageIntegrationDetails {

  private static final String INTEGRATION_DETAIL_TABLE = "integration_details";

  @Override
  public void getOrganizationsStorageIntegrationDetails(String query, String totalRecords, int offset, int limit,
                                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.get(INTEGRATION_DETAIL_TABLE, IntegrationDetail.class, IntegrationDetailCollection.class, query, offset, limit, okapiHeaders, vertxContext,
      GetOrganizationsStorageIntegrationDetailsResponse.class, asyncResultHandler);
  }

  @Override
  public void postOrganizationsStorageIntegrationDetails(IntegrationDetail entity,
                                                         Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(INTEGRATION_DETAIL_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageIntegrationDetailsResponse.class, asyncResultHandler);
  }

  @Override
  public void putOrganizationsStorageIntegrationDetailsById(String id, IntegrationDetail entity,
                                                            Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(INTEGRATION_DETAIL_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStorageIntegrationDetailsByIdResponse.class,
      asyncResultHandler);
  }

  @Override
  public void getOrganizationsStorageIntegrationDetailsById(String id, Map<String, String> okapiHeaders,
                                                            Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(INTEGRATION_DETAIL_TABLE, IntegrationDetail.class, id, okapiHeaders, vertxContext, GetOrganizationsStorageIntegrationDetailsByIdResponse.class,
      asyncResultHandler);
  }

  @Override
  public void deleteOrganizationsStorageIntegrationDetailsById(String id, Map<String, String> okapiHeaders,
                                                               Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(INTEGRATION_DETAIL_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStorageIntegrationDetailsByIdResponse.class,
      asyncResultHandler);
  }
}
