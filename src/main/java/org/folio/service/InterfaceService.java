package org.folio.service;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.dao.DbUtils;
import org.folio.persist.CriterionBuilder;
import org.folio.rest.persist.Conn;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.utils.ResponseUtils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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
      .onSuccess(rowSet -> {
        logger.info("Interface '{}' and associated data were successfully deleted", id);
        asyncResultHandler.handle(ResponseUtils.buildNoContentResponse());
      })
      .onFailure(throwable -> {
        logger.error("Failed to delete interface '{}' or associated data", id, throwable);
        asyncResultHandler.handle(ResponseUtils.buildErrorResponse(throwable));
      });
  }

  private Future<RowSet<Row>> deleteCredentialByInterfaceId(Conn conn, String id) {
    logger.debug("Trying to delete credential by interfaceId: {}", id);
    Criterion criterion = new CriterionBuilder().with("interfaceId", id).build();
    return conn.delete(INTERFACE_CREDENTIALS_TABLE, criterion)
      .onSuccess(rowSet -> logger.info("{} credential with interfaceId '{}' has been deleted", rowSet.rowCount(), id))
      .onFailure(e -> logger.warn("Failed to delete credential by interfaceId: {}", id, e));
  }

  private Future<Void> deleteInterfaceById(Conn conn, String id) {
    logger.debug("Trying to delete interface by id: {}", id);
    return conn.delete(INTERFACE_TABLE, id)
      .compose(DbUtils::verifyEntityUpdate)
      .onSuccess(rowSet -> logger.info("Interface '{}' has been deleted", id))
      .onFailure(e -> logger.warn("Failed to delete interface by id: {}", id, e));
  }

}
