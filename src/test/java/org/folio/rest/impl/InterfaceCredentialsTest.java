package org.folio.rest.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.MalformedURLException;

import org.folio.rest.jaxrs.model.InterfaceCredential;
import org.folio.rest.utils.TestEntities;
import org.junit.jupiter.api.Test;

import io.restassured.response.Response;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class InterfaceCredentialsTest extends TestBase {
  private final Logger logger = LoggerFactory.getLogger(InterfaceCredentialsTest.class);

  private static final String PASSWORD_FIELD = "password";
  private static final String MY_NEW_PASSWORD = "my_new_password";

  private static final String INTERFACE_ID = "14e81009-0f98-45a0-b8e6-e25547beb22f";
  private static final String CREDENTIAL_ID = "426abc2f-b5da-492a-91e2-9615d2edf13d";
  private static final String ANOTHER_INTERFACE_ID = "cd592659-77aa-4eb3-ac34-c9a4657bb20f";

  private static final String INTERFACE_ENDPOINT = TestEntities.INTERFACE.getEndpoint();
  private static final String INTERFACE_ENDPOINT_WITH_ID = TestEntities.INTERFACE.getEndpointWithId();
  private static final String INTERFACE_CREDENTIAL_ENDPOINT_WITH_PARAM = "/organizations-storage/interfaces/%s/credentials";
  private static final String INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID = "/organizations-storage/interfaces/{id}/credentials";

  private static final String INTERFACE_CREDENTIAL_ENDPOINT = String.format(INTERFACE_CREDENTIAL_ENDPOINT_WITH_PARAM, INTERFACE_ID);
  private static final String ANOTHER_INTERFACE_CREDENTIAL_ENDPOINT = String.format(INTERFACE_CREDENTIAL_ENDPOINT_WITH_PARAM, ANOTHER_INTERFACE_ID);

  private static final String SAMPLE_INTERFACE_FILE_1 = "data/interfaces/alexs_interface.json";
  private static final String SAMPLE_INTERFACE_FILE_2 = "data/interfaces/amaz_interface.json";
  private static final String SAMPLE_CREDENTIAL_FILE_1 = "data/interface_credentials/alexs_interface_credential.json";
  private static final String SAMPLE_CREDENTIAL_FILE_2 = "data/interface_credentials/amaz_interface_credential.json";

  private static final String simpleClassName = InterfaceCredential.class.getSimpleName();


  @Test
  public void testDeleteInterfaceWithCredential() throws MalformedURLException {
    // prepare interface and credential data
    postData(INTERFACE_ENDPOINT, getFile(SAMPLE_INTERFACE_FILE_1));
    postData(INTERFACE_CREDENTIAL_ENDPOINT, getFile(SAMPLE_CREDENTIAL_FILE_1));

    // check that data exist
    testEntitySuccessfullyFetched(INTERFACE_ENDPOINT_WITH_ID, INTERFACE_ID);

    getDataById(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, INTERFACE_ID)
      .then()
      .statusCode(200)
      .body("id", equalTo(CREDENTIAL_ID));

    // delete data
    deleteDataSuccess(INTERFACE_ENDPOINT_WITH_ID, INTERFACE_ID);

    // check that interface has been deleted
    testVerifyEntityDeletion(INTERFACE_ENDPOINT_WITH_ID, INTERFACE_ID);

    // check that interface credential has been deleted
    testVerifyEntityDeletion(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, INTERFACE_ID);
  }

  @Test
  public void testInterfaceCredentialsCrud() throws MalformedURLException {
    try {
      logger.info(String.format("--- mod-organizations-storage %s test: Creating %s ... ", simpleClassName, simpleClassName));

      // prepare interface
      String interfaceSample_1 = getFile(SAMPLE_INTERFACE_FILE_1);
      postData(INTERFACE_ENDPOINT, interfaceSample_1).then().statusCode(201);

      String sample = getFile(SAMPLE_CREDENTIAL_FILE_1);
      Response response = postData(INTERFACE_CREDENTIAL_ENDPOINT, sample);

      logger.info(String.format("--- mod-organizations-storage %s test: Valid fields exists ... ", simpleClassName));
      JsonObject sampleJson = JsonObject.mapFrom(new JsonObject(sample).mapTo(InterfaceCredential.class));

      JsonObject responseJson = JsonObject.mapFrom(response.then().extract().response().as(InterfaceCredential.class));
      testAllFieldsExists(responseJson, sampleJson);

      logger.info(String.format("--- mod-organizations-storage %s test: Fetching %s with ID: %s", simpleClassName, simpleClassName, INTERFACE_ID));
      InterfaceCredential createdIC = getData(INTERFACE_CREDENTIAL_ENDPOINT).then()
        .log().ifValidationFails()
        .statusCode(200).log().ifValidationFails()
        .extract()
        .body().as(InterfaceCredential.class);

      assertEquals(INTERFACE_ID, createdIC.getInterfaceId());

      logger.info(String.format("--- mod-organizations-storage %s test: Editing %s with ID: %s", simpleClassName, simpleClassName, INTERFACE_ID));
      createdIC.setPassword(MY_NEW_PASSWORD);
      JsonObject catJSON = JsonObject.mapFrom(createdIC);
      testEntityEdit(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, catJSON.toString(), INTERFACE_ID);

      logger.info(String.format("--- mod-organizations-storage %s test: Fetching updated %s with ID: %s", simpleClassName, simpleClassName, INTERFACE_ID));
      String existedValue = getData(INTERFACE_CREDENTIAL_ENDPOINT)
        .then()
        .log().ifValidationFails()
        .statusCode(200).log().ifValidationFails()
          .extract()
            .body()
              .jsonPath()
              .get(PASSWORD_FIELD).toString();
      assertEquals(MY_NEW_PASSWORD, existedValue);

    } catch (Exception e) {
      logger.error(String.format("--- mod-organizations-storage-test: %s API ERROR: %s", simpleClassName, e.getMessage()));
      fail(e.getMessage());
    } finally {
      logger.info(String.format("--- mod-organizations-storages %s test: Deleting %s with ID: %s", simpleClassName, simpleClassName, INTERFACE_ID));
      deleteDataSuccess(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, INTERFACE_ID);

      logger.info(String.format("--- mod-organizations-storages %s test: Verify %s is deleted with ID: %s", simpleClassName, simpleClassName, INTERFACE_ID));
      testVerifyEntityDeletion(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, INTERFACE_ID);

      deleteDataSuccess(INTERFACE_ENDPOINT_WITH_ID, INTERFACE_ID);
      testVerifyEntityDeletion(INTERFACE_ENDPOINT_WITH_ID, INTERFACE_ID);
    }
  }

  @Test
  public void testFetchEntityWithNonExistedId() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s get by id test: Invalid %s: %s", simpleClassName,simpleClassName, NON_EXISTED_ID));
    getDataById(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, NON_EXISTED_ID).then().log().ifValidationFails()
      .statusCode(404);
  }

  @Test
  public void testEditEntityWithNonExistedId() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s put by id test: Invalid %s: %s", simpleClassName, simpleClassName, NON_EXISTED_ID));
    String sampleData = getFile(SAMPLE_CREDENTIAL_FILE_1);
    putData(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, INTERFACE_ID, sampleData)
      .then().log().ifValidationFails()
      .statusCode(404);
  }

  @Test
  public void testDeleteEntityWithNonExistedId() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s delete by id test: Invalid %s: %s", simpleClassName, simpleClassName, NON_EXISTED_ID));
    deleteData(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, NON_EXISTED_ID)
      .then().log().ifValidationFails()
      .statusCode(404);
  }

  @Test
  public void testEntityWithMismatchId() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s put by id test: Invalid %s: %s", simpleClassName, simpleClassName, NON_EXISTED_ID));

    // prepare interface data
    String interfaceSample_1 = getFile(SAMPLE_INTERFACE_FILE_1);
    postData(INTERFACE_ENDPOINT, interfaceSample_1).then().statusCode(201);
    String interfaceSample_2 = getFile(SAMPLE_INTERFACE_FILE_2);
    postData(INTERFACE_ENDPOINT, interfaceSample_2).then().statusCode(201);

    String sampleCredential_1 = getFile(SAMPLE_CREDENTIAL_FILE_1);
    // create interface credential with id = INTERFACE_ID
    postData(INTERFACE_CREDENTIAL_ENDPOINT, sampleCredential_1).then().statusCode(201);

    String sampleCredential_2 = getFile(SAMPLE_CREDENTIAL_FILE_2);
    // create interface credential with id = ANOTHER_INTERFACE_ID
    postData(ANOTHER_INTERFACE_CREDENTIAL_ENDPOINT, sampleCredential_2).then().statusCode(201);

    // try to create interface credential with mismatched id
    postData(INTERFACE_CREDENTIAL_ENDPOINT, sampleCredential_2).then().statusCode(400);

    // update interface credential with mismatched id
    putData(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, INTERFACE_ID, sampleCredential_2)
      .then().log().ifValidationFails()
      .statusCode(400);

    // interface credentials cleanup
    deleteDataSuccess(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, INTERFACE_ID);
    deleteDataSuccess(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, ANOTHER_INTERFACE_ID);

    // interfaces cleanup
    deleteDataSuccess(INTERFACE_ENDPOINT_WITH_ID, INTERFACE_ID);
    deleteDataSuccess(INTERFACE_ENDPOINT_WITH_ID, ANOTHER_INTERFACE_ID);
  }
}
