package org.folio.rest.impl;

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.config.ApplicationConfig;
import org.folio.rest.resource.interfaces.InitAPI;
import org.folio.spring.SpringContextUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * The class initializes vertx context adding spring context
 */
public class InitAPIs implements InitAPI {
  private static final Logger log = LogManager.getLogger(InitAPIs.class);

  @Override
  public void init(Vertx vertx, Context context, Handler<AsyncResult<Boolean>> resultHandler) {
    vertx.executeBlocking((Callable<Void>) () -> {
        SpringContextUtil.init(vertx, context, ApplicationConfig.class);
        return null;
      }).onComplete(result -> {
        if (result.succeeded()) {
          log.info("APIs initialized successfully");
          resultHandler.handle(Future.succeededFuture(true));
        } else {
          log.error("Failure to init API", result.cause());
          resultHandler.handle(Future.failedFuture(result.cause()));
        }
      });
  }
}
