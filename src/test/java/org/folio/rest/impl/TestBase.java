package org.folio.rest.impl;

import static io.restassured.RestAssured.given;
import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.RestVerticle.OKAPI_HEADER_TOKEN;
import static org.folio.rest.impl.StorageTestSuite.initSpringContext;
import static org.folio.rest.impl.StorageTestSuite.storageUrl;
import static org.folio.rest.utils.TenantApiTestUtil.USER_ID_HEADER;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.IOUtils;
import org.folio.config.ApplicationConfig;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.utils.TestEntities;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * When not run from StorageTestSuite then this class invokes StorageTestSuite.before() and
 * StorageTestSuite.after() to allow to run a single test class, for example from within an
 * IDE during development.
 */
@Log4j2
public abstract class TestBase {

  public static final String NON_EXISTED_ID = "bad500aa-aaaa-500a-aaaa-aaaaaaaaaaaa";
  public static final String TENANT_NAME = "diku";
  public static final Header TENANT_HEADER = new Header(OKAPI_HEADER_TENANT, TENANT_NAME);
  public static final Header X_OKAPI_TOKEN = new Header(OKAPI_HEADER_TOKEN, "eyJhbGciOiJIUzI1NiJ9");

  private static boolean invokeStorageTestSuiteAfter = false;
  private static PostgresClient pgClient;

  @BeforeAll
  public static void testBaseBeforeClass() throws InterruptedException, ExecutionException, TimeoutException {
    Vertx vertx = StorageTestSuite.getVertx();
    if (vertx == null) {
      invokeStorageTestSuiteAfter = true;
      StorageTestSuite.before();
    }

    pgClient =
      PgUtil.postgresClient(
        StorageTestSuite.getVertx().getOrCreateContext(),
        Map.of(TENANT_HEADER.getName(), TENANT_HEADER.getValue()));

    initSpringContext(ApplicationConfig.class);
  }

  @AfterAll
  public static void testBaseAfterClass() throws InterruptedException, ExecutionException, TimeoutException {
    if (invokeStorageTestSuiteAfter) {
      StorageTestSuite.after();
    }
  }

  @SneakyThrows
  protected static void createTables(String schemaName) {
    log.info("creating tables for schema: {}", schemaName);

    try (InputStream tableInput = TenantSampleDataTest.class.getClassLoader().getResourceAsStream(schemaName)) {
      String sqlFile = IOUtils.toString(Objects.requireNonNull(tableInput), StandardCharsets.UTF_8);
      CompletableFuture<Void> schemaCreated = new CompletableFuture<>();
      PostgresClient.getInstance(StorageTestSuite.getVertx()).runSQLFile(sqlFile, false)
        .onComplete(listAsyncResult -> schemaCreated.complete(null));
      schemaCreated.get(60, TimeUnit.SECONDS);
    }
  }

  RequestSpecification commonRequestSpec(){
    return new RequestSpecBuilder().
      addHeader(TENANT_HEADER.getName(), TENANT_HEADER.getValue()).
      addHeader(USER_ID_HEADER.getName(), USER_ID_HEADER.getValue()).
      addHeader(X_OKAPI_TOKEN.getName(), X_OKAPI_TOKEN.getValue()).
      setContentType(ContentType.JSON).
      build();
  }

  void verifyCollectionQuantity(String endpoint, int quantity, Header tenantHeader) {
    log.info("verifyCollectionQuantity:: Endpoint: {}, expected quantity: {}", endpoint, quantity);
    getData(endpoint, tenantHeader)
      .then()
      .log().all()
      .statusCode(200)
      .body("totalRecords", equalTo(quantity));
  }

  void verifyCollectionQuantity(String endpoint, int quantity) {
    // Verify that there are no existing  records
    verifyCollectionQuantity(endpoint, quantity,TENANT_HEADER);
  }

  Response getData(String endpoint, Header tenantHeader) {
    return given()
      .header(tenantHeader)
      .contentType(ContentType.JSON)
      .get(storageUrl(endpoint));
  }

  Response getData(String endpoint) {
    return getData(endpoint, TENANT_HEADER);
  }

  void testVerifyEntityDeletion(String endpoint, String id) {
    getDataById(endpoint, id)
      .then()
      .statusCode(404);
  }

  Response getDataById(String endpoint, String id) {
    return given()
      .pathParam("id", id)
      .header(TENANT_HEADER)
      .contentType(ContentType.JSON)
      .get(storageUrl(endpoint));
  }

  Response postData(String endpoint, String input) {
    return given()
      .header(TENANT_HEADER)
      .accept(ContentType.JSON)
      .contentType(ContentType.JSON)
      .body(input)
      .post(storageUrl(endpoint));
  }

  Response postDataWithUserId(String endpoint, String input) {
    return given()
      .header(TENANT_HEADER)
      .header(USER_ID_HEADER)
      .accept(ContentType.JSON)
      .contentType(ContentType.JSON)
      .body(input)
      .post(storageUrl(endpoint));
  }

  Response putData(String endpoint, String id, String input) {
    return given()
      .pathParam("id", id)
      .header(TENANT_HEADER)
      .contentType(ContentType.JSON)
      .body(input)
      .put(storageUrl(endpoint));
  }

  void deleteDataSuccess(String endpoint, String id) {
    deleteData(endpoint, id)
      .then().log().ifValidationFails()
      .statusCode(204);
  }

  Response deleteData(String endpoint, String id) {
    return deleteData(endpoint, id, TENANT_HEADER);
  }

  Response deleteData(String endpoint, String id, Header tenantHeader) {
    return given()
      .pathParam("id", id)
      .header(tenantHeader)
      .contentType(ContentType.JSON)
      .delete(storageUrl(endpoint));
  }

  void testEntityEdit(String endpoint, String entitySample, String id) {
    putData(endpoint, id, entitySample)
      .then().log().ifValidationFails()
      .statusCode(204);
  }

  void testFetchingUpdatedEntity(String id, TestEntities subObject) {
    String existedValue = getDataById(subObject.getEndpointWithId(), id)
      .then()
      .statusCode(200).log().ifValidationFails()
      .extract()
      .body()
      .jsonPath()
      .get(subObject.getUpdatedFieldName()).toString();
    assertEquals(existedValue, subObject.getUpdatedFieldValue());
  }

  void testInvalidCQLQuery(String endpoint) {
    getData(endpoint).then().log().ifValidationFails()
      .statusCode(400);
  }

  void testEntitySuccessfullyFetched(String endpoint, String id) {
    getDataById(endpoint, id)
      .then().log().ifValidationFails()
      .statusCode(200)
      .body("id", equalTo(id));
  }

  void testAllFieldsExists(JsonObject extracted, JsonObject sampleObject) {
    Set<String> fieldsNames = sampleObject.fieldNames();
    for (String fieldName : fieldsNames) {
      Object sampleField = sampleObject.getValue(fieldName);
      if (sampleField instanceof JsonObject) {
        testAllFieldsExists((JsonObject) sampleField, (JsonObject) extracted.getValue(fieldName));
      } else {
        assertEquals(sampleObject.getValue(fieldName).toString(), extracted.getValue(fieldName).toString());
      }
    }
  }

  String getFile(String filename) {
    String value = "";
    try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filename)) {
      if (inputStream != null) {
        value = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
      }
    } catch (Exception e) {
      value = "";
    }
    return value;
  }

  Future<Void> runSQLTx(String sqlString, boolean startFirst, String failureMessage) {
    String sql =
      startFirst ? sqlString + "SELECT pg_sleep(1);\n" : "SELECT pg_sleep(0.5);\n" + sqlString;
    return pgClient
      .runSQLFile(sql, true)
      .flatMap(
        list ->
          list.isEmpty() ? Future.succeededFuture() : Future.failedFuture(failureMessage));
  }
}
