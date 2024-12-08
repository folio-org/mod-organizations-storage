package org.folio.rest.impl;

import static org.folio.rest.utils.TenantApiTestUtil.prepareTenant;
import static org.folio.rest.utils.TenantApiTestUtil.purge;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.util.List;

import org.folio.rest.jaxrs.model.Setting;
import org.folio.rest.jaxrs.model.SettingCollection;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.core.json.Json;
import io.vertx.junit5.VertxExtension;

@ExtendWith(VertxExtension.class)
class OrganizationSettingsTest extends TestBase {

  private static final String SETTINGS_ENDPOINT = "/organizations-storage/settings";
  private static final String SETTINGS_ENDPOINT_WITH_ID = SETTINGS_ENDPOINT + "/{id}";
  private static final String SAMPLE_SETTING_KEY = "BANKING_INFORMATION_ENABLED";
  private static final String SAMPLE_SETTING_VALUE_OLD = "false";
  private static final String SAMPLE_SETTING_VALUE_NEW = "true";

  @AfterAll
  static void afterAll() {
    purge(TENANT_HEADER);
    prepareTenant(TENANT_HEADER, false, false);
  }

  @Test
  void testOrganizationSettingEndpoints() throws MalformedURLException {

    Setting setting = assertGetCollection(SAMPLE_SETTING_VALUE_OLD);
    String settingId = setting.getId();

    int statusCode = getDataById(SETTINGS_ENDPOINT_WITH_ID, settingId).getStatusCode();
    assertEquals(200, statusCode);

    setting.setValue(SAMPLE_SETTING_VALUE_NEW);
    putData(SETTINGS_ENDPOINT_WITH_ID, settingId, Json.encode(setting));

    assertGetCollection(SAMPLE_SETTING_VALUE_NEW);

    deleteData(SETTINGS_ENDPOINT_WITH_ID, settingId);
    assertEquals(0, getSettings().size());

    postData(SETTINGS_ENDPOINT, Json.encode(setting));
    assertGetCollection(SAMPLE_SETTING_VALUE_NEW);
  }

  @NotNull
  private Setting assertGetCollection(String settingValue) throws MalformedURLException {
    List<Setting> settings = getSettings();
    assertEquals(1, settings.size());
    Setting setting = settings.get(0);
    assertEquals(SAMPLE_SETTING_KEY, setting.getKey());
    assertEquals(settingValue, setting.getValue());
    return setting;
  }

  private List<Setting> getSettings() throws MalformedURLException {
    return getData(SETTINGS_ENDPOINT)
      .then()
      .log()
      .ifValidationFails()
      .statusCode(200)
      .extract()
      .as(SettingCollection.class)
      .getSettings();
  }

}
