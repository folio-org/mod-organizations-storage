package org.folio.service;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.folio.util.ResponseUtils.handleFailure;
import static org.folio.util.ResponseUtils.handleNoContentResponse;

import javax.ws.rs.core.Response;

import org.folio.persist.CriterionBuilder;
import org.folio.persist.Tx;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.Criteria.Criterion;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.handler.impl.HttpStatusException;

public class InterfaceService {

  private static final String INTERFACE_CREDENTIALS_TABLE = "interface_credentials";
  private static final String INTERFACE_TABLE = "interfaces";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final PostgresClient pgClient;

  public InterfaceService(PostgresClient pgClient) {
    this.pgClient = pgClient;
  }

  public void deleteOrganizationsInterfaceById(String id,
    Context vertxContext, Handler<AsyncResult<Response>> asyncResultHandler) {
    vertxContext.runOnContext(v -> {
      Tx<String> tx = new Tx<>(id, pgClient);
      tx.startTx()
        .compose(this::deleteCredentialByInterfaceId)
        .compose(this::deleteInterfaceById)
        .compose(Tx::endTx)
        .onComplete(handleNoContentResponse(asyncResultHandler, tx, "Interface {} {} deleted"));
    });
  }

  private Future<Tx<String>> deleteCredentialByInterfaceId(Tx<String> tx) {
    logger.info("Delete credential by InterfaceId={}", tx.getEntity());

    Promise<Tx<String>> promise = Promise.promise();
    Criterion criterion = new CriterionBuilder()
      .with("interfaceId", tx.getEntity()).build();
    pgClient.delete(tx.getConnection(), INTERFACE_CREDENTIALS_TABLE, criterion, reply -> {
      if (reply.failed()) {
        handleFailure(promise, reply);
      } else {
        logger.info("{} credential with InterfaceId={} has been deleted", reply.result().rowCount(), tx.getEntity());
        promise.complete(tx);
      }
    });
    return promise.future();
  }

  private Future<Tx<String>> deleteInterfaceById(Tx<String> tx) {
    Promise<Tx<String>> promise = Promise.promise();
    pgClient.delete(tx.getConnection(), INTERFACE_TABLE, tx.getEntity(), reply -> {
      if (reply.result().rowCount() == 0) {
        promise.fail(new HttpStatusException(NOT_FOUND.getStatusCode(), NOT_FOUND.getReasonPhrase()));
      } else {
        promise.complete(tx);
      }
    });
    return promise.future();
  }
}
