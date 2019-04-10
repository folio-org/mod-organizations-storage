package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Category;
import org.folio.rest.jaxrs.model.CategoryCollection;
import org.folio.rest.jaxrs.resource.OrganizationStorageCategories;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

public class CategoriesAPI implements OrganizationStorageCategories {
  private static final String CATEGORY_TABLE = "category";

  private String idFieldName = "id";

  public CategoriesAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getOrganizationStorageCategories(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      EntitiesMetadataHolder<Category, CategoryCollection> entitiesMetadataHolder = new EntitiesMetadataHolder<>(Category.class, CategoryCollection.class, GetOrganizationStorageCategoriesResponse.class, "setCategories");
      QueryHolder cql = new QueryHolder(CATEGORY_TABLE, query, offset, limit);
      getEntitiesCollection(entitiesMetadataHolder, cql, asyncResultHandler, vertxContext, okapiHeaders);
    });
  }

  @Override
  @Validate
  public void postOrganizationStorageCategories(String lang, org.folio.rest.jaxrs.model.Category entity,
                                       Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(CATEGORY_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationStorageCategoriesResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationStorageCategoriesById(String id, String lang, Map<String, String> okapiHeaders,
                                          Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(CATEGORY_TABLE, Category.class, id, okapiHeaders,vertxContext, GetOrganizationStorageCategoriesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationStorageCategoriesById(String id, String lang, Map<String, String> okapiHeaders,
                                             Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(CATEGORY_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationStorageCategoriesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationStorageCategoriesById(String id, String lang, org.folio.rest.jaxrs.model.Category entity,
                                          Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(CATEGORY_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationStorageCategoriesByIdResponse.class, asyncResultHandler);
  }
}
