package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Category;
import org.folio.rest.jaxrs.model.CategoryCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageCategories;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class CategoriesAPI implements OrganizationsStorageCategories {
  private static final String CATEGORY_TABLE = "categories";

  @Override
  @Validate
  public void getOrganizationsStorageCategories(String query, String totalRecords, int offset, int limit, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.get(CATEGORY_TABLE, Category.class, CategoryCollection.class, query, offset, limit, okapiHeaders, vertxContext,
        GetOrganizationsStorageCategoriesResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStorageCategories(org.folio.rest.jaxrs.model.Category entity,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(CATEGORY_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageCategoriesResponse.class,
        asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageCategoriesById(String id, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(CATEGORY_TABLE, Category.class, id, okapiHeaders, vertxContext,
        GetOrganizationsStorageCategoriesByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageCategoriesById(String id, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(CATEGORY_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStorageCategoriesByIdResponse.class,
        asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageCategoriesById(String id, org.folio.rest.jaxrs.model.Category entity,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(CATEGORY_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStorageCategoriesByIdResponse.class,
        asyncResultHandler);
  }
}
