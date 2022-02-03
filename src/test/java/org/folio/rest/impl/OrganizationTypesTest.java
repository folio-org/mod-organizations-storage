package org.folio.rest.impl;

import static org.folio.rest.utils.TenantApiTestUtil.prepareTenant;
import static org.folio.rest.utils.TenantApiTestUtil.purge;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Set;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.utils.ModuleName;
import org.folio.rest.utils.TestEntities;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class OrganizationTypesTest extends TestBase {

  private static final String ORGANIZATION_TYPES_ENDPOINT =
      TestEntities.ORGANIZATION_TYPE.getEndpoint();
  private static final String ORGANIZATION_TYPES_ENDPOINT_WITH_ID =
      TestEntities.ORGANIZATION_TYPE.getEndpointWithId();
  private static final String ORGANIZATIONS_ENDPOINT = TestEntities.ORGANIZATION.getEndpoint();
  private static final String ORGANIZATIONS_ENDPOINT_WITH_ID =
      TestEntities.ORGANIZATION.getEndpointWithId();
  private static final String ORGANIZATION_SAMPLE_FILE = "organization.sample";
  private static final String ORGANIZATION_TYPE_SAMPLE_FILE = "organization_type.sample";
  private static final String NONEXISTENT_ORGANIZATION_TYPE_ID =
      "bee174d5-0737-4cc3-9d21-332cf473acee";
  private static final String SAMPLE_ORGANIZATION_TYPE_ID = "f04c7277-0019-43cf-84b3-02d894a9d81a";
  private static final String SAMPLE_ORGANIZATION_ID = "80fb5168-cdf1-11e8-a8d5-f2801f1b9fd1";
  private static final String ORGANIZATIONS_TABLE = "organizations";
  private static final String ORGANIZATION_TYPES_TABLE = "organization_types";

  private static PostgresClient pgClient;
  private static String deleteSQL;
  private static String updateSQL;

  @BeforeAll
  static void beforeAll() {
    pgClient =
        PgUtil.postgresClient(
            StorageTestSuite.getVertx().getOrCreateContext(),
            Map.of(TENANT_HEADER.getName(), TENANT_HEADER.getValue()));

    String schema = TENANT_HEADER.getValue() + "_" + ModuleName.getModuleName();
    String searchPathSQL = String.format("SET search_path TO %s;", schema);
    deleteSQL =
        String.format(
            "%s DELETE FROM %s WHERE id = '%s';",
            searchPathSQL, ORGANIZATION_TYPES_TABLE, SAMPLE_ORGANIZATION_TYPE_ID);
    updateSQL =
        String.format(
            "%s UPDATE %s SET jsonb = jsonb_set(jsonb, '{organizationTypes}', '[ \"%s\" ]', true) "
                + "WHERE id = '%s';",
            searchPathSQL,
            ORGANIZATIONS_TABLE,
            SAMPLE_ORGANIZATION_TYPE_ID,
            SAMPLE_ORGANIZATION_ID);
  }

  @AfterAll
  static void afterAll() {
    purge(TENANT_HEADER);
    prepareTenant(TENANT_HEADER, false, false);
  }

  private String getOrganizationWithType(String organization, String typeId) {
    return Json.encode(
        Json.decodeValue(organization, Organization.class).withOrganizationTypes(Set.of(typeId)));
  }

  private Future<Void> runSQLTx(String sqlString, boolean startFirst, String failureMessage) {
    String sql =
        startFirst ? sqlString + "SELECT pg_sleep(1);\n" : "SELECT pg_sleep(0.5);\n" + sqlString;
    return pgClient
        .runSQLFile(sql, true)
        .flatMap(
            list ->
                list.isEmpty() ? Future.succeededFuture() : Future.failedFuture(failureMessage));
  }

  @Test
  void testOrganizationTypesConstraints() throws MalformedURLException {
    final String organizationSample = getFile(ORGANIZATION_SAMPLE_FILE);
    final String organizationSampleWithNonexistentType =
        getOrganizationWithType(organizationSample, NONEXISTENT_ORGANIZATION_TYPE_ID);

    // try to POST organization with nonexistent organization_type
    postData(ORGANIZATIONS_ENDPOINT, organizationSampleWithNonexistentType)
        .then()
        .log()
        .all()
        .statusCode(400);

    // POST organization_type
    String typeId =
        postData(ORGANIZATION_TYPES_ENDPOINT, getFile(ORGANIZATION_TYPE_SAMPLE_FILE))
            .then()
            .statusCode(201)
            .extract()
            .path("id");

    // POST organization with existing organization_type
    String orgId =
        postData(ORGANIZATIONS_ENDPOINT, getOrganizationWithType(organizationSample, typeId))
            .then()
            .statusCode(201)
            .extract()
            .path("id");

    // try to PUT organization with non-existing organization_type
    putData(ORGANIZATIONS_ENDPOINT_WITH_ID, orgId, organizationSampleWithNonexistentType)
        .then()
        .log()
        .all()
        .statusCode(422);

    // try to DELETE organization_type that is referenced by an organization
    deleteData(ORGANIZATION_TYPES_ENDPOINT_WITH_ID, typeId).then().log().all().statusCode(422);

    // DELETE organization first
    deleteDataSuccess(ORGANIZATIONS_ENDPOINT_WITH_ID, orgId);

    // DELETE organization_type
    deleteDataSuccess(ORGANIZATION_TYPES_ENDPOINT_WITH_ID, typeId);
  }

  @Test
  void testAdvisoryLockDeleteFirst(VertxTestContext context) {
    prepareTenant(TENANT_HEADER, true, true);
    CompositeFuture.all(
            runSQLTx(deleteSQL, true, "delete_tx_failed"),
            runSQLTx(updateSQL, false, "update_tx_failed"))
        .onComplete(
            context.failing(
                t ->
                    context
                        .verify(() -> assertThat(t.getMessage(), equalTo("update_tx_failed")))
                        .completeNow()));
  }

  @Test
  void testAdvisoryLockUpdateFirst(VertxTestContext context) {
    prepareTenant(TENANT_HEADER, true, true);
    CompositeFuture.all(
            runSQLTx(updateSQL, true, "update_tx_failed"),
            runSQLTx(deleteSQL, false, "delete_tx_failed"))
        .onComplete(
            context.failing(
                t ->
                    context
                        .verify(() -> assertThat(t.getMessage(), equalTo("delete_tx_failed")))
                        .completeNow()));
  }
}
