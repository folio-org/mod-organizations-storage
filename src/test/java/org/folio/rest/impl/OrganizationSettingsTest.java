package org.folio.rest.impl;

import static org.folio.rest.utils.TenantApiTestUtil.prepareTenant;
import static org.folio.rest.utils.TenantApiTestUtil.purge;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.util.List;

import org.folio.rest.jaxrs.model.Setting;
import org.folio.rest.jaxrs.model.SettingCollection;
import org.folio.rest.tools.utils.ModuleName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;

@ExtendWith(VertxExtension.class)
class OrganizationSettingsTest extends TestBase {

  private static final String SETTINGS_TABLE = "settings";
  private static final String SETTINGS_ENDPOINT = "/organizations-storage/settings";
  private static final String SETTINGS_ENDPOINT_WITH_ID = SETTINGS_ENDPOINT + "/{id}";
  private static final String SAMPLE_SETTING_ID = "f04c7277-0019-43cf-84b3-02d894a9d81a";
  private static final String SAMPLE_SETTING_KEY = "BANKING_INFORMATION_ENABLED";
  private static final String SAMPLE_SETTING_VALUE = "true";

//  private static String deleteSQL;
//  private static String updateSQL;

  @BeforeAll
  static void beforeAll() {
//    String schema = TENANT_HEADER.getValue() + "_" + ModuleName.getModuleName();
//    String searchPathSQL = String.format("SET search_path TO %s;", schema);
//    deleteSQL =
//      String.format(
//        "%s DELETE FROM %s WHERE id = '%s';",
//        searchPathSQL, SETTINGS_TABLE, SAMPLE_SETTING_ID);
//    updateSQL =
//      String.format(
//        "%s UPDATE %s SET name = '%s' WHERE id = '%s';",
//        searchPathSQL,
//        SETTINGS_TABLE,
//        SAMPLE_SETTING_KEY,
//        SAMPLE_SETTING_ID);
  }

  @AfterAll
  static void afterAll() {
    purge(TENANT_HEADER);
    prepareTenant(TENANT_HEADER, false, false);
  }

  @Test
  void testBankingAccountTypeEndpoints() throws MalformedURLException {

    // GET LIST
    List<Setting> settings = getData(SETTINGS_ENDPOINT)
      .then()
      .log().ifValidationFails()
      .statusCode(200).log().ifValidationFails()
      .extract()
      .body().as(SettingCollection.class).getSettings();
    assertEquals(1, settings.size());
    Setting setting = settings.get(0);
    assertEquals(SAMPLE_SETTING_KEY, setting.getKey());
    assertEquals(SAMPLE_SETTING_VALUE, setting.getValue());

    //putData(SETTINGS_ENDPOINT_WITH_ID, setting.getId(), )
  }

}
