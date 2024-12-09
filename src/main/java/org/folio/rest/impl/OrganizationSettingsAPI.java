package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Setting;
import org.folio.rest.jaxrs.model.SettingCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageSettings;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class OrganizationSettingsAPI implements OrganizationsStorageSettings {

  private static final String SETTINGS_TABLE = "settings";

  @Override
  @Validate
  public void getOrganizationsStorageSettings(String query,
                                              String totalRecords,
                                              int offset,
                                              int limit,
                                              Map<String, String> okapiHeaders,
                                              Handler<AsyncResult<Response>> asyncResultHandler,
                                              Context vertxContext) {
    PgUtil.get(SETTINGS_TABLE, Setting.class, SettingCollection.class, query, offset, limit,
      okapiHeaders, vertxContext, GetOrganizationsStorageSettingsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStorageSettings(Setting entity,
                                               Map<String, String> okapiHeaders,
                                               Handler<AsyncResult<Response>> asyncResultHandler,
                                               Context vertxContext) {
    PgUtil.post(SETTINGS_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageSettingsResponse.class,
      asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageSettingsById(String id,
                                                  Map<String, String> okapiHeaders,
                                                  Handler<AsyncResult<Response>> asyncResultHandler,
                                                  Context vertxContext) {
    PgUtil.getById(SETTINGS_TABLE, Setting.class, id, okapiHeaders, vertxContext,
      GetOrganizationsStorageSettingsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageSettingsById(String id,
                                                     Map<String, String> okapiHeaders,
                                                     Handler<AsyncResult<Response>> asyncResultHandler,
                                                     Context vertxContext) {
    PgUtil.deleteById(SETTINGS_TABLE, id, okapiHeaders, vertxContext,
      DeleteOrganizationsStorageSettingsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageSettingsById(String id,
                                                  Setting entity,
                                                  Map<String, String> okapiHeaders,
                                                  Handler<AsyncResult<Response>> asyncResultHandler,
                                                  Context vertxContext) {
    PgUtil.put(SETTINGS_TABLE, entity, id, okapiHeaders, vertxContext,
      PutOrganizationsStorageSettingsByIdResponse.class, asyncResultHandler);
  }

}
