package org.folio.service;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.persist.CriterionBuilder;
import org.folio.rest.persist.Conn;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.util.ResponseUtils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.ext.web.handler.HttpException;

public class InterfaceService {

  private static final Logger logger = LogManager.getLogger(InterfaceService.class);

  private static final String INTERFACE_CREDENTIALS_TABLE = "interface_credentials";
  private static final String INTERFACE_TABLE = "interfaces";

  private final PostgresClient pgClient;

  public InterfaceService(PostgresClient pgClient) {
    this.pgClient = pgClient;
  }

  public void deleteOrganizationsInterfaceById(String id, Handler<AsyncResult<Response>> asyncResultHandler) {
    pgClient.withTrans(
      conn ->
        deleteCredentialByInterfaceId(conn, id)
          .compose(conn1 -> deleteInterfaceById(conn1, id))
          .onComplete(result -> ResponseUtils.handleNoContentResponse(result, id, "Interface '{}' '{}' deleted", asyncResultHandler))
    );
  }

  private Future<Conn> deleteCredentialByInterfaceId(Conn conn, String id) {
    logger.debug("Trying to delete credential by interfaceId: {}", id);

    Promise<Conn> promise = Promise.promise();
    Criterion criterion = new CriterionBuilder()
      .with("interfaceId", id).build();
    pgClient.delete(INTERFACE_CREDENTIALS_TABLE, criterion, reply -> {
      if (reply.failed()) {
        logger.warn("Failed to delete credential by interfaceId: {}", id, reply.cause());
        ResponseUtils.handleFailure(promise, reply);
      } else {
        logger.info("{} credential with interfaceId '{}' has been deleted", reply.result().rowCount(), id);
        promise.complete(conn);
      }
    });
    return promise.future();
  }

  private Future<Conn> deleteInterfaceById(Conn conn, String id) {
    logger.debug("Trying to delete interface by id: {}", id);
    Promise<Conn> promise = Promise.promise();
    pgClient.delete(INTERFACE_TABLE, id, reply -> {
      if (reply.result().rowCount() == 0) {
        logger.warn("Failed to delete interface by id: {}", id);
        promise.fail(new HttpException(NOT_FOUND.getStatusCode(), NOT_FOUND.getReasonPhrase()));
      } else {
        logger.info("Interface '{}' has been deleted", id);
        promise.complete(conn);
      }
    });
    return promise.future();
  }

}
