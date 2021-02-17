package org.folio.rest.impl;

import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.utils.TenantApiTestUtil.deleteTenant;
import static org.folio.rest.utils.TenantApiTestUtil.postTenant;
import static org.folio.rest.utils.TenantApiTestUtil.prepareTenant;
import static org.folio.rest.utils.TenantApiTestUtil.purge;
import static org.folio.rest.utils.TestEntities.ORGANIZATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

import java.net.MalformedURLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationCollection;
import org.folio.rest.jaxrs.model.TenantAttributes;
import org.folio.rest.jaxrs.model.TenantJob;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.utils.TenantApiTestUtil;
import org.folio.rest.utils.TestEntities;
import org.junit.jupiter.api.Test;

import io.restassured.http.Header;


class TenantSampleDataTest extends TestBase {

  private final Logger logger = LogManager.getLogger(TenantSampleDataTest.class);

  private static final String TEST_EXPECTED_QUANTITY_FOR_ENTRY = "Test expected {} quantity for {}";

  private static final Header NONEXISTENT_TENANT_HEADER = new Header(OKAPI_HEADER_TENANT, "no_tenant");
  private static final Header ANOTHER_TENANT_HEADER = new Header(OKAPI_HEADER_TENANT, "new_tenant");
  private static final Header PARTIAL_TENANT_HEADER = new Header(OKAPI_HEADER_TENANT, "partial_tenant");
  private static TenantJob tenantJob;


  @Test
  void sampleDataTests() throws MalformedURLException {
    try {
      logger.info("-- create a tenant with no sample/reference data --");
      tenantJob = prepareTenant(ANOTHER_TENANT_HEADER, false, false);
      logger.info("-- upgrade the tenant with sample & reference data, so that it will be inserted now --");
      tenantJob = upgradeTenantWithDataLoad();
      logger.info(
          "-- upgrade the tenant again with no sample/reference data, so the previously inserted data stays in tact --");
      tenantJob = upgradeTenantWithNoDataLoad();
    }
    finally {
      deleteTenant(tenantJob ,ANOTHER_TENANT_HEADER);
    }
  }


  @Test
  void testPartialSampleDataLoading() throws MalformedURLException {
    logger.info("load sample date");

    try {
      TenantAttributes tenantAttributes = TenantApiTestUtil.prepareTenantBody(true, false);
      tenantJob = postTenant(PARTIAL_TENANT_HEADER, tenantAttributes);

      OrganizationCollection organizationCollection = getData(ORGANIZATION.getEndpoint(), PARTIAL_TENANT_HEADER).then()
        .extract()
        .response()
        .as(OrganizationCollection.class);

      for (Organization organization : organizationCollection.getOrganizations()) {
        deleteData(ORGANIZATION.getEndpointWithId(), organization.getId(), PARTIAL_TENANT_HEADER);
      }

      tenantAttributes = TenantApiTestUtil.prepareTenantBody(true, true);
      tenantJob = postTenant(PARTIAL_TENANT_HEADER, tenantAttributes);

      for (TestEntities entity : TestEntities.values()) {
        logger.info(TEST_EXPECTED_QUANTITY_FOR_ENTRY, entity.getInitialQuantity(), entity.name());
        verifyCollectionQuantity(entity.getEndpoint(), entity.getInitialQuantity(), PARTIAL_TENANT_HEADER);
      }
    } finally {
      deleteTenant(tenantJob, PARTIAL_TENANT_HEADER);
    }
  }


  @Test
  void testLoadReferenceData() throws MalformedURLException {
    logger.info("load only Reference Data");
    try {
      TenantAttributes tenantAttributes = TenantApiTestUtil.prepareTenantBody(false, true);
      tenantJob = postTenant(PARTIAL_TENANT_HEADER, tenantAttributes);

      verifyCollectionQuantity("/organizations-storage/categories", 4, PARTIAL_TENANT_HEADER);
      for (TestEntities entity : TestEntities.values()) {
        //category is the only reference data, which must be loaded
        if (!entity.equals(TestEntities.CATEGORY)) {
          logger.info("Test sample data not loaded for " + entity.name());
          verifyCollectionQuantity(entity.getEndpoint(), 0, PARTIAL_TENANT_HEADER);
        }
      }
    } finally {
      PostgresClient oldClient = PostgresClient.getInstance(StorageTestSuite.getVertx(), PARTIAL_TENANT_HEADER.getValue());
      deleteTenant(tenantJob, PARTIAL_TENANT_HEADER);
      PostgresClient newClient = PostgresClient.getInstance(StorageTestSuite.getVertx(), PARTIAL_TENANT_HEADER.getValue());
      assertThat(oldClient, not(newClient));
    }
  }

  private TenantJob upgradeTenantWithDataLoad() throws MalformedURLException {

    logger.info("upgrading Module with sample and reference data");
    TenantAttributes tenantAttributes = TenantApiTestUtil.prepareTenantBody(true, true);
    tenantJob = postTenant(ANOTHER_TENANT_HEADER, tenantAttributes);
    for (TestEntities entity : TestEntities.values()) {
      logger.info("Test expected quantity for " + entity.name());
      verifyCollectionQuantity(entity.getEndpoint(), entity.getInitialQuantity(), ANOTHER_TENANT_HEADER);
    }
    return tenantJob;
  }

  private TenantJob upgradeTenantWithNoDataLoad() throws MalformedURLException {

    logger.info("upgrading Module without sample data");
    TenantAttributes tenantAttributes = TenantApiTestUtil.prepareTenantBody(false, false);
    tenantJob = postTenant(ANOTHER_TENANT_HEADER, tenantAttributes);

    for(TestEntities te: TestEntities.values()) {
      verifyCollectionQuantity(te.getEndpoint(), te.getInitialQuantity(), ANOTHER_TENANT_HEADER);
    }
    return tenantJob;
  }

  @Test
  void upgradeTenantWithNonExistentDb() throws MalformedURLException {
    logger.info("upgrading Module for non existed tenant");
    TenantAttributes tenantAttributes = TenantApiTestUtil.prepareTenantBody(false, false);
    try {
      // RMB-331 the case if older version has no db schema
      postTenant(NONEXISTENT_TENANT_HEADER, tenantAttributes);

      // Check that no sample data loaded
      for (TestEntities entity : TestEntities.values()) {
        logger.info(TEST_EXPECTED_QUANTITY_FOR_ENTRY, 0, entity.name());
        verifyCollectionQuantity(entity.getEndpoint(), 0, NONEXISTENT_TENANT_HEADER);
      }
    }
    finally {
      purge(NONEXISTENT_TENANT_HEADER);
    }
  }

}
