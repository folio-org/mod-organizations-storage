package org.folio.rest.impl;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.Map;

import org.folio.rest.utils.TestEntities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.restassured.response.Response;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

@RunWith(Parameterized.class)
public class EntitiesCrudTest extends TestBase {

  private final Logger logger = LoggerFactory.getLogger(EntitiesCrudTest.class);

  @Parameterized.Parameter public TestEntities testEntity;

  @Parameterized.Parameters(name = "{index}:{0}")
  public static TestEntities[] data() {
    return TestEntities.values();
  }

  @Test
  public void testPositiveCases() throws MalformedURLException {
    String sampleId = null;
    try {

      logger.info(String.format("--- mod-organizations-storage %s test: Verifying database's initial state ... ", testEntity.name()));
      verifyCollectionQuantity(testEntity.getEndpoint(), 0);

      logger.info(String.format("--- mod-organizations-storage %s test: Creating %s ... ", testEntity.name(), testEntity.name()));
      String sample = getFile(testEntity.getSampleFileName());
      Response response = postData(testEntity.getEndpoint(), sample);
      sampleId = response.then().extract().path("id");

      logger.info(String.format("--- mod-organizations-storage %s test: Valid fields exists ... ", testEntity.name()));
      JsonObject sampleJson = convertToMatchingModelJson(sample, testEntity);
      JsonObject responseJson = JsonObject.mapFrom(response.then().extract().response().as(testEntity.getClazz()));
      testAllFieldsExists(responseJson, sampleJson);

      logger.info(String.format("--- mod-organizations-storage %s test: Verifying only 1 adjustment was created ... ", testEntity.name()));
      verifyCollectionQuantity(testEntity.getEndpoint(),1);

      logger.info(String.format("--- mod-organizations-storage %s test: Fetching %s with ID: %s", testEntity.name(), testEntity.name(), sampleId));
      testEntitySuccessfullyFetched(testEntity.getEndpointWithId(), sampleId);

      logger.info(String.format("--- mod-organizations-storage %s test: Editing %s with ID: %s", testEntity.name(), testEntity.name(), sampleId));
      JsonObject catJSON = new JsonObject(sample);
      catJSON.put("id", sampleId);
      catJSON.put(testEntity.getUpdatedFieldName(), testEntity.getUpdatedFieldValue());
      testEntityEdit(testEntity.getEndpointWithId(), catJSON.toString(), sampleId);

      logger.info(String.format("--- mod-organizations-storage %s test: Fetching updated %s with ID: %s", testEntity.name(), testEntity.name(), sampleId));
      testFetchingUpdatedEntity(sampleId, testEntity);

    } catch (Exception e) {
      logger.error(String.format("--- mod-organizations-storage-test: %s API ERROR: %s", testEntity.name(), e.getMessage()));
      fail(e.getMessage());
    } finally {
      logger.info(String.format("--- mod-organizations-storages %s test: Deleting %s with ID: %s", testEntity.name(), testEntity.name(), sampleId));
      deleteDataSuccess(testEntity.getEndpointWithId(), sampleId);

      logger.info(String.format("--- mod-organizations-storages %s test: Verify %s is deleted with ID: %s", testEntity.name(), testEntity.name(), sampleId));
      testVerifyEntityDeletion(testEntity.getEndpointWithId(), sampleId);
    }

  }

  private JsonObject convertToMatchingModelJson(String sample, TestEntities testEntity) {
    return JsonObject.mapFrom(new JsonObject(sample).mapTo(testEntity.getClazz()));
  }

  @Test
  public void testFetchEntityWithNonExistedId() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s get by id test: Invalid %s: %s", testEntity.name(), testEntity.name(), NON_EXISTED_ID));
    getDataById(testEntity.getEndpointWithId(), NON_EXISTED_ID).then().log().ifValidationFails()
      .statusCode(404);
  }

  @Test
  public void testEditEntityWithNonExistedId() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s put by id test: Invalid %s: %s", testEntity.name(), testEntity.name(), NON_EXISTED_ID));
    String sampleData = getFile(testEntity.getSampleFileName());
    putData(testEntity.getEndpointWithId(), NON_EXISTED_ID, sampleData)
      .then().log().ifValidationFails()
      .statusCode(404);
  }

  @Test
  public void testDeleteEntityWithNonExistedId() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s delete by id test: Invalid %s: %s", testEntity.name(), testEntity.name(), NON_EXISTED_ID));
    deleteData(testEntity.getEndpointWithId(), NON_EXISTED_ID)
      .then().log().ifValidationFails()
      .statusCode(404);
  }

  @Test
  public void testGetEntitiesWithInvalidCQLQuery() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s test: Invalid CQL query", testEntity.name()));
    testInvalidCQLQuery(testEntity.getEndpoint() + "?query=invalid-query");
  }

  @Test
  public void testReceiveMetadata() throws MalformedURLException {
    logger.info(String.format("--- mod-organizations-storage %s test: Test receive metadata", testEntity.name()));
    String sample = getFile(testEntity.getSampleFileName());
    Response response = postDataWithUserId(testEntity.getEndpoint(), sample);
    Map metadata = response.then().extract().path("metadata");
    assertThat(metadata, is(notNullValue()));
  }

}
