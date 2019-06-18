package org.folio.rest.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;

import org.folio.rest.jaxrs.model.InterfaceCredential;
import org.junit.Test;

import io.restassured.response.Response;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class InterfaceCredentialsTest extends TestBase {
  private final Logger logger = LoggerFactory.getLogger(InterfaceCredentialsTest.class);

  private static final String ID = "id";
  private static final String PASSWORD_FIELD = "password";
  private static final String MY_NEW_PASSWORD = "my_new_password";
  private static final String INTERFACE_ID = "60b952a6-5570-44f3-9972-f00c9dcb098e";
  private static final String ANOTHER_INTERFACE_ID = "793b9d42-ae12-41d7-a36a-f33e5b9e82c5";
  private static final String INTERFACE_ENDPOINT = "/organizations-storage/interfaces";
  private static final String INTERFACE_ENDPOINT_WITH_ID = "/organizations-storage/interfaces/{id}";
  private static final String INTERFACE_CREDENTIAL_ENDPOINT_WITH_PARAM = "/organizations-storage/interfaces/%s/credentials";
  private static final String INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID = "/organizations-storage/interfaces/{id}/credentials";
  private static final String INTERFACE_CREDENTIAL_ENDPOINT = String.format(INTERFACE_CREDENTIAL_ENDPOINT_WITH_PARAM, INTERFACE_ID);
  private static final String ANOTHER_INTERFACE_CREDENTIAL_ENDPOINT = String.format(INTERFACE_CREDENTIAL_ENDPOINT_WITH_PARAM, ANOTHER_INTERFACE_ID);
  private static final String SAMPLE_CREDENTIAL_FILE = "interface_credential.sample";
  private static final String simpleClassName = InterfaceCredential.class.getSimpleName();

  @Test
  public void testInterfaceCredentialsCrud() throws MalformedURLException {
    try {
      logger.info(String.format("--- mod-organizations-storage %s test: Creating %s ... ", simpleClassName, simpleClassName));

      // prepare interface
      String interfaceSample_1 = getFile("data/interfaces/acso_interface.json");
      postData(INTERFACE_ENDPOINT, interfaceSample_1).then().statusCode(201);

      String sample = getFile(SAMPLE_CREDENTIAL_FILE);
      Response response = postData(INTERFACE_CREDENTIAL_ENDPOINT, sample);

      logger.info(String.format("--- mod-organizations-storage %s test: Valid fields exists ... ", simpleClassName));
      JsonObject sampleJson = JsonObject.mapFrom(new JsonObject(sample).mapTo(InterfaceCredential.class));

      JsonObject responseJson = JsonObject.mapFrom(response.then().extract().response().as(InterfaceCredential.class));
      testAllFieldsExists(responseJson, sampleJson);

      logger.info(String.format("--- mod-organizations-storage %s test: Fetching %s with ID: %s", simpleClassName, simpleClassName, INTERFACE_ID));
      String id = getData(INTERFACE_CREDENTIAL_ENDPOINT).then()
        .log().ifValidationFails()
        .statusCode(200).log().ifValidationFails()
        .extract()
        .body()
        .jsonPath()
        .get(ID).toString();
      assertEquals(INTERFACE_ID, id);

      logger.info(String.format("--- mod-organizations-storage %s test: Editing %s with ID: %s", simpleClassName, simpleClassName, INTERFACE_ID));
      JsonObject catJSON = new JsonObject(sample);
      catJSON.put(ID, INTERFACE_ID);
      catJSON.put(PASSWORD_FIELD, MY_NEW_PASSWORD);
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
    String sampleData = getFile(SAMPLE_CREDENTIAL_FILE);
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
  public void testEditEntityWithMismatchId() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s put by id test: Invalid %s: %s", simpleClassName, simpleClassName, NON_EXISTED_ID));

    // prepare interface data
    String interfaceSample_1 = getFile("data/interfaces/acso_interface.json");
    postData(INTERFACE_ENDPOINT, interfaceSample_1).then().statusCode(201);
    String interfaceSample_2 = getFile("data/interfaces/gobi_interface.json");
    postData(INTERFACE_ENDPOINT, interfaceSample_2).then().statusCode(201);

    String sample = getFile(SAMPLE_CREDENTIAL_FILE);
    // create interface credential with id = INTERFACE_ID
    postData(INTERFACE_CREDENTIAL_ENDPOINT, sample).then().statusCode(201);

    // create interface credential with id = ANOTHER_INTERFACE_ID
    JsonObject credentialJson = new JsonObject(sample);
    credentialJson.put("id", ANOTHER_INTERFACE_ID);
    sample = credentialJson.toString();
    postData(ANOTHER_INTERFACE_CREDENTIAL_ENDPOINT, sample).then().statusCode(201);

    // update interface credential with mismatched id
    putData(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, INTERFACE_ID, sample)
      .then().log().ifValidationFails()
      .statusCode(400);

    deleteDataSuccess(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, INTERFACE_ID);
    deleteDataSuccess(INTERFACE_CREDENTIAL_ENDPOINT_WITH_ID, ANOTHER_INTERFACE_ID);

    // interfaces cleanup
    deleteDataSuccess(INTERFACE_ENDPOINT_WITH_ID, INTERFACE_ID);
    deleteDataSuccess(INTERFACE_ENDPOINT_WITH_ID, ANOTHER_INTERFACE_ID);
  }
}
