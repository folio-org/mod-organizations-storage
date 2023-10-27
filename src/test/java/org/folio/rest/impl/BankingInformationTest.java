package org.folio.rest.impl;

import io.vertx.core.json.JsonObject;
import org.folio.rest.jaxrs.model.BankingInformation;
import org.folio.rest.jaxrs.model.BankingInformationCollection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import static org.folio.rest.utils.TenantApiTestUtil.prepareTenant;
import static org.folio.rest.utils.TenantApiTestUtil.purge;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankingInformationTest extends TestBase {

  private static final String BANKING_INFORMATION_ENDPOINT = "/organizations-storage/banking-information";
  private static final String BANKING_INFORMATION_ENDPOINT_WITH_ID = "/organizations-storage/banking-information/{id}";

  @AfterAll
  static void afterAll() {
    purge(TENANT_HEADER);
    prepareTenant(TENANT_HEADER, false, false);
  }

  @Test
  void testBankingInformationEndpoints() throws MalformedURLException {
    BankingInformation bankingInformation = new BankingInformation()
      .withId(UUID.randomUUID().toString())
      .withOrganizationId(UUID.randomUUID().toString())
      .withBankName("Bank name")
      .withTransitNumber("123456");

    postData(BANKING_INFORMATION_ENDPOINT, JsonObject.mapFrom(bankingInformation).encodePrettily())
      .then()
      .statusCode(201);

    BankingInformation createdBankingInformation = getData(String.format("%s/%s", BANKING_INFORMATION_ENDPOINT, bankingInformation.getId()))
      .then()
      .log().ifValidationFails()
      .statusCode(200).log().ifValidationFails()
      .extract()
      .body().as(BankingInformation.class);
    assertEquals(bankingInformation.getBankName(), createdBankingInformation.getBankName());

    List<BankingInformation> bankingInformationList = getData(BANKING_INFORMATION_ENDPOINT)
      .then()
      .log().ifValidationFails()
      .statusCode(200).log().ifValidationFails()
      .extract()
      .body().as(BankingInformationCollection.class).getBankingInformation();
    assertEquals(1, bankingInformationList.size());
    assertEquals(bankingInformation.getBankName(), bankingInformationList.get(0).getBankName());

    deleteDataSuccess(BANKING_INFORMATION_ENDPOINT_WITH_ID, bankingInformation.getId());
  }
}
