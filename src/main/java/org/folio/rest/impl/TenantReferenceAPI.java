package org.folio.rest.impl;

import static org.folio.rest.RestVerticle.MODULE_SPECIFIC_ARGS;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.dbschema.Versioned;
import org.folio.rest.jaxrs.model.Parameter;
import org.folio.rest.jaxrs.model.TenantAttributes;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.utils.TenantLoading;
import org.folio.rest.tools.utils.TenantTool;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class TenantReferenceAPI extends TenantAPI {
  private static final Logger log = LogManager.getLogger(TenantReferenceAPI.class);
  private static final String PARAMETER_LOAD_SAMPLE = "loadSample";
  private static final String PARAMETER_LOAD_REFERENCE = "loadReference";

  @Override
  public Future<Integer> loadData(TenantAttributes attributes, String tenantId, Map<String, String> headers, Context vertxContext) {
    log.info("loadData:: Loading reference data for tenant {}", tenantId);
    Vertx vertx = vertxContext.owner();
    Promise<Integer> promise = Promise.promise();

    TenantLoading tl = new TenantLoading();
    buildDataLoadingParameters(attributes, tl);

    tl.perform(attributes, headers, vertx, res1 -> {
      if (res1.failed()) {
        log.warn("loadData:: Failed to load reference data for tenant {}", tenantId, res1.cause());
        promise.fail(res1.cause());
      } else {
        promise.complete(res1.result());
      }
    });

    return promise.future();
  }

  private void buildDataLoadingParameters(TenantAttributes tenantAttributes, TenantLoading tl) {
    if (isLoadSample(tenantAttributes)) {
      if (isNew(tenantAttributes, "3.2.0")) {
        tl.withKey(PARAMETER_LOAD_SAMPLE)
        .withLead("data")
        .add("organizations-3.2.0", "organizations-storage/organizations");
      }
      if (isNew(tenantAttributes, "1.1.0")) {
        tl.withKey(PARAMETER_LOAD_SAMPLE)
        .withLead("data")
        .add("contacts-1.1.0", "organizations-storage/contacts");
      }
      if (isNew(tenantAttributes, "2.0.0")) {
        tl.withKey(PARAMETER_LOAD_SAMPLE)
        .withLead("data")
        .add("interfaces-2.0.0", "organizations-storage/interfaces");
      }
    }
    if (isLoadReference(tenantAttributes)) {
      if (isNew(tenantAttributes, "1.0.0")) {
        tl.withKey(PARAMETER_LOAD_REFERENCE)
        .withLead("data")
        .add("categories-1.0.0", "organizations-storage/categories");
      }
      if (isNew(tenantAttributes, "4.3.0")) {
        tl.withKey(PARAMETER_LOAD_REFERENCE)
        .withLead("data")
        .add("organization_types-4.3.0", "organizations-storage/organization-types");
      }
    }
  }

  /**
   * Returns attributes.getModuleFrom() < featureVersion or attributes.getModuleFrom() is null.
   */
  private static boolean isNew(TenantAttributes attributes, String featureVersion) {
    if (attributes.getModuleFrom() == null) {
      return true;
    }
    var since = new Versioned() {
    };
    since.setFromModuleVersion(featureVersion);
    return since.isNewForThisInstall(attributes.getModuleFrom());
  }

  private boolean isLoadSample(TenantAttributes tenantAttributes) {
    log.info("isLoadSample:: Checking if sample data should be loaded");
    // if a system parameter is passed from command line, ex: loadSample=true
    // that value is considered,Priority of Parameters:
    // Tenant Attributes > command line parameter > default(false)
    boolean loadSample = Boolean.parseBoolean(MODULE_SPECIFIC_ARGS.getOrDefault(PARAMETER_LOAD_SAMPLE, "false"));
    List<Parameter> parameters = tenantAttributes.getParameters();
    for (Parameter parameter : parameters) {
      if (PARAMETER_LOAD_SAMPLE.equals(parameter.getKey())) {
        loadSample = Boolean.parseBoolean(parameter.getValue());
      }
    }
    log.info("isLoadSample:: result: {}", loadSample);
    return loadSample;

  }

  private boolean isLoadReference(TenantAttributes tenantAttributes) {
    log.info("isLoadReference:: Checking if reference data needs to be loaded for tenant");
    // if a system parameter is passed from command line, ex: loadReference=true
    // that value is considered, Priority of Parameters:
    // Tenant Attributes > command line parameter > default(false)
    boolean loadReference = Boolean.parseBoolean(MODULE_SPECIFIC_ARGS.getOrDefault(PARAMETER_LOAD_REFERENCE, "false"));
    List<Parameter> parameters = tenantAttributes.getParameters();
    for (Parameter parameter : parameters) {
      if (PARAMETER_LOAD_REFERENCE.equals(parameter.getKey())) {
        loadReference = Boolean.parseBoolean(parameter.getValue());
      }
    }
    log.info("isLoadReference:: result: {}", loadReference);
    return loadReference;

  }

  @Override
  public void deleteTenantByOperationId(String operationId, Map<String, String> headers, Handler<AsyncResult<Response>> handler,
      Context ctx) {
    log.info("deleteTenantByOperationId:: Deleting tenant by operationId: {}", operationId);
    super.deleteTenantByOperationId(operationId, headers, res -> {
      Vertx vertx = ctx.owner();
      String tenantId = TenantTool.tenantId(headers);
      PostgresClient.getInstance(vertx, tenantId).closeClient(event -> handler.handle(res));
    }, ctx);
  }

}
