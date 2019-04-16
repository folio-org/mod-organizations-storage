package org.folio.rest.impl;

import static org.folio.rest.RestVerticle.MODULE_SPECIFIC_ARGS;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.folio.rest.jaxrs.model.Parameter;
import org.folio.rest.jaxrs.model.TenantAttributes;
import org.folio.rest.tools.utils.TenantLoading;

public class TenantReferenceAPI extends TenantAPI {
  private static final Logger log = LoggerFactory.getLogger(TenantReferenceAPI.class);

  private static final String PARAMETER_LOAD_SAMPLE = "loadSample";
  private static final String PARAMETER_LOAD_REFERENCE = "loadReference";

  @Override
  public void postTenant(TenantAttributes tenantAttributes, Map<String, String> headers,
      Handler<AsyncResult<Response>> hndlr, Context cntxt) {
    log.info("postTenant");
    Vertx vertx = cntxt.owner();
    super.postTenant(tenantAttributes, headers, res -> {
      if (res.failed() || (res.succeeded() && (res.result().getStatus() < 200 || res.result().getStatus() > 299))) {
        hndlr.handle(res);
        return;
      }

      TenantLoading tl = new TenantLoading();
      boolean loadData = buildDataLoadingParameters(tenantAttributes, tl);

      if (loadData) {
        tl.perform(tenantAttributes, headers, vertx, res1 -> {
          if (res1.failed()) {
            hndlr.handle(io.vertx.core.Future.succeededFuture(PostTenantResponse
              .respond500WithTextPlain(res1.cause().getLocalizedMessage())));
            return;
          }
          hndlr.handle(io.vertx.core.Future.succeededFuture(PostTenantResponse
            .respond201WithApplicationJson("")));
        });
      } else {
        hndlr.handle(res);
        return;
      }

    }, cntxt);

  }

  private boolean buildDataLoadingParameters(TenantAttributes tenantAttributes, TenantLoading tl) {
    boolean loadData = false;
    if (isLoadSample(tenantAttributes)) {
      tl.withKey(PARAMETER_LOAD_SAMPLE)
        .withLead("data")
        .add("organizations", "organizations-storage/organizations")
        .add("contacts", "organizations-storage/contacts")
        .add("interfaces", "organizations-storage/interfaces");
      loadData = true;
    }
    if (isLoadReference(tenantAttributes)) {
      tl.withKey(PARAMETER_LOAD_REFERENCE)
        .withLead("data")
        .add("categories", "organizations-storage/categories");
      loadData = true;
    }
    return loadData;
  }

  private boolean isLoadSample(TenantAttributes tenantAttributes) {
    // if a system parameter is passed from command line, ex: loadSample=true
    // that value is considered,Priority of Parameters:
    // Tenant Attributes > command line parameter > default(false)
    boolean loadSample = Boolean.parseBoolean(MODULE_SPECIFIC_ARGS.getOrDefault(PARAMETER_LOAD_SAMPLE,
        "false"));
    List<Parameter> parameters = tenantAttributes.getParameters();
    for (Parameter parameter : parameters) {
      if (PARAMETER_LOAD_SAMPLE.equals(parameter.getKey())) {
        loadSample = Boolean.parseBoolean(parameter.getValue());
      }
    }
    return loadSample;

  }

  private boolean isLoadReference(TenantAttributes tenantAttributes) {
    // if a system parameter is passed from command line, ex: loadReference=true
    // that value is considered,Priority of Parameters:
    // Tenant Attributes > command line parameter > default(false)
    boolean loadReference = Boolean.parseBoolean(MODULE_SPECIFIC_ARGS.getOrDefault(PARAMETER_LOAD_REFERENCE,
        "false"));
    List<Parameter> parameters = tenantAttributes.getParameters();
    for (Parameter parameter : parameters) {
      if (PARAMETER_LOAD_REFERENCE.equals(parameter.getKey())) {
        loadReference = Boolean.parseBoolean(parameter.getValue());
      }
    }
    return loadReference;

  }

  @Override
  public void getTenant(Map<String, String> headers, Handler<AsyncResult<Response>> hndlr, Context cntxt) {
    log.info("getTenant");
    super.getTenant(headers, hndlr, cntxt);
  }

  @Override
  public void deleteTenant(Map<String, String> headers, Handler<AsyncResult<Response>> hndlr, Context cntxt) {
    log.info("deleteTenant");
    super.deleteTenant(headers, hndlr, cntxt);
  }
}
