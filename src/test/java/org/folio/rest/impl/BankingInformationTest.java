package org.folio.rest.impl;

import io.vertx.core.json.JsonObject;
import org.folio.rest.jaxrs.model.BankingInformation;
import org.folio.rest.jaxrs.model.BankingInformationCollection;
import org.folio.rest.utils.TestEntities;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import static org.folio.rest.utils.TenantApiTestUtil.prepareTenant;
import static org.folio.rest.utils.TenantApiTestUtil.purge;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BankingInformationTest extends TestBase {

  private static final String BANKING_INFORMATION_ENDPOINT = "/organizations-storage/banking-information";
  private static final String BANKING_INFORMATION_ENDPOINT_WITH_ID = "/organizations-storage/banking-information/{id}";
  private static final String ORGANIZATIONS_ENDPOINT = TestEntities.ORGANIZATION.getEndpoint();
  private static final String ORGANIZATION_SAMPLE_FILE = "organization.sample";

  @AfterAll
  static void afterAll() {
    purge(TENANT_HEADER);
    prepareTenant(TENANT_HEADER, false, false);
  }

  @Test
  void testBankingInformationEndpoints() throws MalformedURLException {
    String organizationId =
      postData(ORGANIZATIONS_ENDPOINT, getFile(ORGANIZATION_SAMPLE_FILE))
        .then()
        .statusCode(201)
        .extract()
        .path("id");

    BankingInformation bankingInformation = new BankingInformation()
      .withId(UUID.randomUUID().toString())
      .withOrganizationId(organizationId)
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

    createdBankingInformation.setBankName("New Bank name");

    putData(BANKING_INFORMATION_ENDPOINT_WITH_ID, createdBankingInformation.getId(), JsonObject.mapFrom(createdBankingInformation).encodePrettily())
      .then()
      .statusCode(204);

    List<BankingInformation> bankingInformationList = getData(BANKING_INFORMATION_ENDPOINT)
      .then()
      .log().ifValidationFails()
      .statusCode(200).log().ifValidationFails()
      .extract()
      .body().as(BankingInformationCollection.class).getBankingInformation();
    assertEquals(1, bankingInformationList.size());
    assertEquals(createdBankingInformation.getBankName(), bankingInformationList.get(0).getBankName());

    deleteDataSuccess(BANKING_INFORMATION_ENDPOINT_WITH_ID, bankingInformation.getId());
  }
}
