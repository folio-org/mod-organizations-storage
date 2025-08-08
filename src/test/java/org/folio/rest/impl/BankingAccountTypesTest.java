package org.folio.rest.impl;

import static org.folio.rest.utils.TenantApiTestUtil.prepareTenant;
import static org.folio.rest.utils.TenantApiTestUtil.purge;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.util.List;

import org.folio.okapi.common.GenericCompositeFuture;
import org.folio.rest.jaxrs.model.BankingAccountType;
import org.folio.rest.jaxrs.model.BankingAccountTypeCollection;
import org.folio.rest.tools.utils.ModuleName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
class BankingAccountTypesTest extends TestBase {

  private static final String BANKING_ACCOUNT_TYPES_TABLE = "banking_account_types";
  private static final String BANKING_ACCOUNT_TYPES_ENDPOINT = "/organizations-storage/banking-account-types";
  private static final String BANKING_ACCOUNT_TYPES_ENDPOINT_WITH_ID = BANKING_ACCOUNT_TYPES_ENDPOINT + "/{id}";
  private static final String BANKING_ACCOUNT_TYPE_SAMPLE_FILE = "banking_account_type.sample";
  private static final String SAMPLE_BANKING_ACCOUNT_TYPE_ID = "f04c7277-0019-43cf-84b3-02d894a9d81a";
  private static final String SAMPLE_BANKING_ACCOUNT_TYPE_NAME = "Personal";

  private static String deleteSQL;
  private static String updateSQL;

  @BeforeAll
  static void beforeAll() {
    String schema = TENANT_HEADER.getValue() + "_" + ModuleName.getModuleName();
    String searchPathSQL = String.format("SET search_path TO %s;", schema);
    deleteSQL =
      String.format(
        "%s DELETE FROM %s WHERE id = '%s';",
        searchPathSQL, BANKING_ACCOUNT_TYPES_TABLE, SAMPLE_BANKING_ACCOUNT_TYPE_ID);
    updateSQL =
      String.format(
        "%s UPDATE %s SET name = '%s' WHERE id = '%s';",
        searchPathSQL,
        BANKING_ACCOUNT_TYPES_TABLE,
        SAMPLE_BANKING_ACCOUNT_TYPE_NAME,
        SAMPLE_BANKING_ACCOUNT_TYPE_ID);
  }

  @AfterAll
  static void afterAll() {
    purge(TENANT_HEADER);
    prepareTenant(TENANT_HEADER, false, false);
  }

  @Test
  void testBankingAccountTypeEndpoints() throws MalformedURLException {
    // POST
    String typeId =
      postData(BANKING_ACCOUNT_TYPES_ENDPOINT, getFile(BANKING_ACCOUNT_TYPE_SAMPLE_FILE))
        .then()
        .statusCode(201)
        .extract()
        .path("id");

    // GET
    BankingAccountType bankingAccountType = getData(BANKING_ACCOUNT_TYPES_ENDPOINT + "/" + typeId)
      .then()
      .log().ifValidationFails()
      .statusCode(200).log().ifValidationFails()
      .extract()
      .body().as(BankingAccountType.class);
    assertEquals(SAMPLE_BANKING_ACCOUNT_TYPE_NAME, bankingAccountType.getName());

    // GET LIST
    List<BankingAccountType> bankingAccountTypes = getData(BANKING_ACCOUNT_TYPES_ENDPOINT)
      .then()
      .log().ifValidationFails()
      .statusCode(200).log().ifValidationFails()
      .extract()
      .body().as(BankingAccountTypeCollection.class).getBankingAccountTypes();
    assertEquals(1, bankingAccountTypes.size());
    assertEquals(SAMPLE_BANKING_ACCOUNT_TYPE_NAME, bankingAccountTypes.get(0).getName());

    // DELETE
    deleteDataSuccess(BANKING_ACCOUNT_TYPES_ENDPOINT_WITH_ID, typeId);
  }

  @Test
  void testAdvisoryLockDeleteFirst(VertxTestContext context) {
    prepareTenant(TENANT_HEADER, true, true);
    GenericCompositeFuture.all(List.of(
        runSQLTx(deleteSQL, true, "delete_tx_failed"),
        runSQLTx(updateSQL, false, "update_tx_failed")))
      .onComplete(
        context.failing(
          t ->
            context
              .verify(() -> assertThat(t.getMessage(), equalTo("update_tx_failed")))
              .completeNow()));
  }
}
