package org.folio.rest.impl;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.folio.rest.impl.StorageTestSuite.storageUrl;

public class AuditOutboxAPITest extends TestBase {

  public static final String AUDIT_OUTBOX_ENDPOINT = "/organizations-storage/audit-outbox/process";

  @Test
  void testPostInvoiceStorageAuditOutboxProcess() {
    given()
      .spec(commonRequestSpec())
      .when()
      .post(storageUrl(AUDIT_OUTBOX_ENDPOINT))
      .then()
      .assertThat()
      .statusCode(200)
      .extract()
      .response();
  }
}
