package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.rest.jaxrs.resource.OrganizationsStorageAuditOutbox;
import org.folio.service.audit.AuditOutboxService;
import org.folio.spring.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class AuditOutboxAPI implements OrganizationsStorageAuditOutbox {

  private static final Logger log = LogManager.getLogger();

  @Autowired
  private AuditOutboxService auditOutboxService;

  public AuditOutboxAPI() {
    SpringContextUtil.autowireDependencies(this, Vertx.currentContext());
  }

  @Override
  public void postOrganizationsStorageAuditOutboxProcess(Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler,
                                                         Context vertxContext) {
    auditOutboxService.processOutboxEventLogs(okapiHeaders, vertxContext)
      .onSuccess(res -> asyncResultHandler.handle(Future.succeededFuture(Response.status(Response.Status.OK).build())))
      .onFailure(cause -> {
        log.warn("Processing of outbox events table has failed", cause);
        asyncResultHandler.handle(Future.failedFuture(cause));
      });
  }
}
