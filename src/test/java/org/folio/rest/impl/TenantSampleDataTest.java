package org.folio.rest.impl;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationCollection;
import org.folio.rest.utils.TenantApiTestUtil;
import org.folio.rest.utils.TestEntities;
import org.junit.Test;

import java.net.MalformedURLException;

import static io.restassured.RestAssured.given;
import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.impl.StorageTestSuite.storageUrl;
import static org.folio.rest.utils.TenantApiTestUtil.TENANT_ENDPOINT;
import static org.folio.rest.utils.TenantApiTestUtil.deleteTenant;
import static org.folio.rest.utils.TenantApiTestUtil.postToTenant;
import static org.folio.rest.utils.TenantApiTestUtil.prepareTenant;
import static org.folio.rest.utils.TestEntities.ORGANIZATION;


public class TenantSampleDataTest extends TestBase{

  private final Logger logger = LoggerFactory.getLogger(TenantSampleDataTest.class);

  private static final Header NONEXISTENT_TENANT_HEADER = new Header(OKAPI_HEADER_TENANT, "no_tenant");
  private static final Header ANOTHER_TENANT_HEADER = new Header(OKAPI_HEADER_TENANT, "new_tenant");
  private static final Header ANOTHER_TENANT_HEADER_WITHOUT_UPGRADE = new Header(OKAPI_HEADER_TENANT, "no_upgrade_tenant");
  private static final Header PARTIAL_TENANT_HEADER = new Header(OKAPI_HEADER_TENANT, "partial_tenant");


  @Test
  public void isTenantCreated() throws MalformedURLException {
    getData(TENANT_ENDPOINT)
      .then()
      .assertThat()
      .statusCode(200);
  }

  @Test
  public void sampleDataTests() throws MalformedURLException {
    try {
      logger.info("-- create a tenant with no sample data --");
      prepareTenant(ANOTHER_TENANT_HEADER, false, false);
      logger.info("-- upgrade the tenant with sample & reference data, so that it will be inserted now --");
      upgradeTenantWithDataLoad();
      logger.info(
          "-- upgrade the tenant again with no sample/reference data, so the previously inserted data stays in tact --");
      upgradeTenantWithNoDataLoad();
      upgradeNonExistentTenant();
    } finally {
      deleteTenant(ANOTHER_TENANT_HEADER);
    }
  }

  @Test
  public void failIfNoUrlToHeader() throws MalformedURLException {
    JsonObject jsonBody = TenantApiTestUtil.prepareTenantBody(true, true, false);
    given()
        .header(new Header(OKAPI_HEADER_TENANT, "noURL"))
        .contentType(ContentType.JSON)
      .body(jsonBody.encodePrettily())
      .post(storageUrl(TENANT_ENDPOINT))
        .then()
        .assertThat()
        .statusCode(500);
  }

  @Test
  public void testLoadDataWithoutUpgrade() throws MalformedURLException {
    logger.info("load sample data");
    try{
      JsonObject jsonBody = TenantApiTestUtil.prepareTenantBody(true, true, false);
      postToTenant(ANOTHER_TENANT_HEADER_WITHOUT_UPGRADE, jsonBody)
        .assertThat()
        .statusCode(201);
      for (TestEntities entity : TestEntities.values()) {
        logger.info("Test expected quantity for " + entity.name());
        verifyCollectionQuantity(entity.getEndpoint(), entity.getInitialQuantity(), ANOTHER_TENANT_HEADER_WITHOUT_UPGRADE);
      }
    } finally {
      deleteTenant(ANOTHER_TENANT_HEADER_WITHOUT_UPGRADE);
    }
  }

  @Test
  public void testPartialSampleDataLoading() throws MalformedURLException {
    logger.info("load sample date");

    try{
      JsonObject jsonBody = TenantApiTestUtil.prepareTenantBody(true, false, false);
      postToTenant(PARTIAL_TENANT_HEADER, jsonBody)
        .assertThat()
        .statusCode(201);
      OrganizationCollection organizationCollection = getData(ORGANIZATION.getEndpoint(), PARTIAL_TENANT_HEADER)
        .then()
        .extract()
        .response()
        .as(OrganizationCollection.class);

      for (Organization organization : organizationCollection.getOrganizations()) {
        deleteData(ORGANIZATION.getEndpointWithId(), organization.getId(), PARTIAL_TENANT_HEADER);
      }

      jsonBody = TenantApiTestUtil.prepareTenantBody(true, true, true);
      postToTenant(PARTIAL_TENANT_HEADER, jsonBody)
        .assertThat()
        .statusCode(201);

      for (TestEntities entity : TestEntities.values()) {
        logger.info("Test expected quantity for " + entity.name());
        verifyCollectionQuantity(entity.getEndpoint(), entity.getInitialQuantity(), PARTIAL_TENANT_HEADER);
      }
    } finally {
      deleteTenant(PARTIAL_TENANT_HEADER);
    }
  }


  @Test
  public void testLoadReferenceData() throws MalformedURLException {
    logger.info("load only Reference Data");
    try {
      JsonObject jsonBody = TenantApiTestUtil.prepareTenantBody(false, true, false);
      postToTenant(ANOTHER_TENANT_HEADER_WITHOUT_UPGRADE, jsonBody)
        .assertThat()
        .statusCode(201);
      verifyCollectionQuantity("/organization-storage/categories", 4, ANOTHER_TENANT_HEADER_WITHOUT_UPGRADE);
      for (TestEntities entity : TestEntities.values()) {
        logger.info("Test sample data not loaded for " + entity.name());
        verifyCollectionQuantity(entity.getEndpoint(), 0, ANOTHER_TENANT_HEADER_WITHOUT_UPGRADE);
      }
    } finally {
      deleteTenant(ANOTHER_TENANT_HEADER_WITHOUT_UPGRADE);
    }
  }

  private void upgradeTenantWithDataLoad() throws MalformedURLException {

    logger.info("upgrading Module with sample and reference data");
    JsonObject jsonBody = TenantApiTestUtil.prepareTenantBody(true, true, true);
    postToTenant(ANOTHER_TENANT_HEADER, jsonBody)
      .assertThat()
      .statusCode(201);
    for (TestEntities entity : TestEntities.values()) {
      logger.info("Test expected quantity for " + entity.name());
      verifyCollectionQuantity(entity.getEndpoint(), entity.getInitialQuantity(), ANOTHER_TENANT_HEADER);
    }
  }

  private void upgradeTenantWithNoDataLoad() throws MalformedURLException {

    logger.info("upgrading Module without sample data");

    JsonObject jsonBody = TenantApiTestUtil.prepareTenantBody(false, false, true);
    postToTenant(ANOTHER_TENANT_HEADER, jsonBody)
      .assertThat()
      .statusCode(200);
  }


  private void upgradeNonExistentTenant() throws MalformedURLException {

    logger.info("upgrading Module for non existed tenant");

    JsonObject jsonBody = TenantApiTestUtil.prepareTenantBody(false, false, true);
    postToTenant(NONEXISTENT_TENANT_HEADER, jsonBody)
      .assertThat()
      .statusCode(400);
  }

}
