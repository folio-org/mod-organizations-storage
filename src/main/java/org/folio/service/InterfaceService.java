package org.folio.service;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.persist.CriterionBuilder;
import org.folio.rest.persist.Conn;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.util.ResponseUtils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class InterfaceService {

  private static final Logger logger = LogManager.getLogger(InterfaceService.class);

  private static final String INTERFACE_CREDENTIALS_TABLE = "interface_credentials";
  private static final String INTERFACE_TABLE = "interfaces";

  private final PostgresClient pgClient;

  public InterfaceService(PostgresClient pgClient) {
    this.pgClient = pgClient;
  }

  public void deleteOrganizationsInterfaceById(String id, Handler<AsyncResult<Response>> asyncResultHandler) {
    pgClient.withTrans(conn -> deleteCredentialByInterfaceId(conn, id)
        .compose(rowSet -> deleteInterfaceById(conn, id)))
      .onComplete(result -> ResponseUtils.handleNoContentResponse(result, id, asyncResultHandler));
  }

  private Future<RowSet<Row>> deleteCredentialByInterfaceId(Conn conn, String id) {
    logger.debug("Trying to delete credential by interfaceId: {}", id);
    Criterion criterion = new CriterionBuilder().with("interfaceId", id).build();
    return conn.delete(INTERFACE_CREDENTIALS_TABLE, criterion)
      .recover(ResponseUtils::handleFailure)
      .onSuccess(rowSet -> logger.info("{} credential with interfaceId '{}' has been deleted", rowSet.rowCount(), id))
      .onFailure(e -> logger.warn("Failed to delete credential by interfaceId: {}", id, e));
  }

  private Future<RowSet<Row>> deleteInterfaceById(Conn conn, String id) {
    logger.debug("Trying to delete interface by id: {}", id);
    return conn.delete(INTERFACE_TABLE, id)
      .compose(rowSet -> {
        if (rowSet.rowCount() == 0) {
          return Future.failedFuture(new HttpException(NOT_FOUND.getStatusCode(), NOT_FOUND.getReasonPhrase()));
        } else {
          return Future.succeededFuture(rowSet);
        }
      })
      .recover(ResponseUtils::handleFailure)
      .onSuccess(rowSet -> logger.info("Interface '{}' has been deleted", id))
      .onFailure(e -> logger.warn("Failed to delete interface by id: {}", id, e));
  }

}
