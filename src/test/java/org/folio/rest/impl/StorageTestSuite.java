package org.folio.rest.impl;

import static org.folio.rest.impl.TestBase.TENANT_HEADER;
import static org.folio.rest.utils.TenantApiTestUtil.deleteTenant;
import static org.folio.rest.utils.TenantApiTestUtil.prepareTenant;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.postgres.testing.PostgresTesterContainer;
import org.folio.rest.RestVerticle;
import org.folio.rest.jaxrs.model.TenantJob;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.client.test.HttpClientMock2;
import org.folio.rest.tools.utils.NetworkUtils;
import org.folio.spring.SpringContextUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import io.restassured.http.Header;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxImpl;
import io.vertx.core.json.JsonObject;

@RunWith(JUnitPlatform.class)
public class StorageTestSuite {
  private static final Logger logger = LogManager.getLogger(StorageTestSuite.class);

  private static Vertx vertx;
  private final static int port = NetworkUtils.nextFreePort();
  public static final Header URL_TO_HEADER = new Header("X-Okapi-Url-to", "http://localhost:" + port);
  private static TenantJob tenantJob;

  private StorageTestSuite() {}

  public static URL storageUrl(String path) {
    try {
      return new URL("http", "localhost", port, path);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(path, e);
    }
  }

  public static Vertx getVertx() {
    return vertx;
  }

  public static void initSpringContext(Class<?> defaultConfiguration) {
    SpringContextUtil.init(vertx, getFirstContextFromVertx(vertx), defaultConfiguration);
  }

  private static Context getFirstContextFromVertx(Vertx vertx) {
    return vertx.deploymentIDs().stream()
      .flatMap(id -> ((VertxImpl) vertx).getDeployment(id).getVerticles().stream())
      .map(StorageTestSuite::getContextWithReflection)
      .filter(Objects::nonNull)
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("Spring context was not created"));
  }

  private static Context getContextWithReflection(Verticle verticle) {
    try {
      Field field = AbstractVerticle.class.getDeclaredField("context");
      field.setAccessible(true);
      return ((Context) field.get(verticle));
    } catch (NoSuchFieldException | IllegalAccessException ignored) {
      return null;
    }
  }

  @BeforeAll
  public static void before() throws InterruptedException, ExecutionException, TimeoutException {

    // tests expect English error messages only, no Danish/German/...
    Locale.setDefault(Locale.US);
    vertx = Vertx.vertx();

    logger.info("Start container database");

    PostgresClient.setPostgresTester(new PostgresTesterContainer());

    DeploymentOptions options = new DeploymentOptions();

    options.setConfig(new JsonObject().put("http.port", port).put(HttpClientMock2.MOCK_MODE, "true"));
    startVerticle(options);

    tenantJob = prepareTenant(TENANT_HEADER, false, false);
  }

  @AfterAll
  public static void after() throws InterruptedException, ExecutionException, TimeoutException {
    logger.info("Delete tenant");
    deleteTenant(tenantJob, TENANT_HEADER);

    CompletableFuture<String> undeploymentComplete = new CompletableFuture<>();

    vertx.close(res -> {
      if(res.succeeded()) {
        undeploymentComplete.complete(null);
      }
      else {
        undeploymentComplete.completeExceptionally(res.cause());
      }
    });

    undeploymentComplete.get(20, TimeUnit.SECONDS);
    logger.info("Stop database");
    PostgresClient.stopPostgresTester();
  }

  private static void startVerticle(DeploymentOptions options)
    throws InterruptedException, ExecutionException, TimeoutException {

    logger.info("Start verticle");

    CompletableFuture<String> deploymentComplete = new CompletableFuture<>();

    vertx.deployVerticle(RestVerticle.class.getName(), options, res -> {
      if(res.succeeded()) {
        deploymentComplete.complete(res.result());
      }
      else {
        deploymentComplete.completeExceptionally(res.cause());
      }
    });

    deploymentComplete.get(60, TimeUnit.SECONDS);
  }

  @Nested
  class TenantSampleDataTestNested extends TenantSampleDataTest {
  }

  @Nested
  class EntitiesCrudTestNested extends EntitiesCrudTest {
  }

  @Nested
  class InterfaceCredentialsTestNested extends InterfaceCredentialsTest {
  }

  @Nested
  class OrganizationTypesTestNested extends OrganizationTypesTest {
  }

  @Nested
  class BankingAccountTypesTestNested extends BankingAccountTypesTest {
  }

  @Nested
  class OrganizationSettingsTestNested extends OrganizationSettingsTest {
  }

  @Nested
  class BankingInformationTestNested extends BankingInformationTest {
  }
}
