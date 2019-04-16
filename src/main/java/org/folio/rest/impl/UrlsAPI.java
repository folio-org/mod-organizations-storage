package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Url;
import org.folio.rest.jaxrs.model.UrlCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageUrls;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

public class UrlsAPI implements OrganizationsStorageUrls {
  private static final String URL_TABLE = "urls";

  private String idFieldName = "id";

  public UrlsAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  @Validate
  public void getOrganizationsStorageUrls(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      EntitiesMetadataHolder<Url, UrlCollection> entitiesMetadataHolder = new EntitiesMetadataHolder<>(Url.class, UrlCollection.class, GetOrganizationsStorageUrlsResponse.class);
      QueryHolder cql = new QueryHolder(URL_TABLE, query, offset, limit);
      getEntitiesCollection(entitiesMetadataHolder, cql, asyncResultHandler, vertxContext, okapiHeaders);
    });
  }

  @Override
  @Validate
  public void postOrganizationsStorageUrls(String lang, org.folio.rest.jaxrs.model.Url entity,
                                        Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(URL_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageUrlsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageUrlsById(String id, String lang, Map<String, String> okapiHeaders,
                                           Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(URL_TABLE, Url.class, id, okapiHeaders,vertxContext, GetOrganizationsStorageUrlsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageUrlsById(String id, String lang, Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(URL_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStorageUrlsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageUrlsById(String id, String lang, org.folio.rest.jaxrs.model.Url entity,
                                           Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(URL_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStorageUrlsByIdResponse.class, asyncResultHandler);
  }
}
