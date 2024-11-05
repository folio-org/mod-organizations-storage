package org.folio.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.persist.CriterionBuilder;
import org.folio.rest.impl.OrganizationsAPI;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.PostgresClient;
import org.folio.util.DbUtils;
import org.folio.util.ResponseUtils;

import javax.ws.rs.core.Response;

public class OrganizationService {
  private final Logger logger = LogManager.getLogger(OrganizationsAPI.class);

  private static final String ORGANIZATION_TABLE = "organizations";
  private static final String BANKING_INFORMATION_TABLE = "banking_information";

  private final PostgresClient pgClient;

  public OrganizationService(PostgresClient pgClient) {
    this.pgClient = pgClient;
  }

  public void deleteOrganizationById(String id, Handler<AsyncResult<Response>> asyncResultHandler) {
    Criterion criterion = new CriterionBuilder().with("organizationId", id).build();
    pgClient.withTrans(conn ->
      conn.delete(BANKING_INFORMATION_TABLE, criterion)
        .compose(res -> {
          logger.info("Deleted {} records from table {}", res.rowCount(), BANKING_INFORMATION_TABLE);
          return conn.delete(ORGANIZATION_TABLE, id)
            .compose(DbUtils::failOnNoUpdateOrDelete);
        })
    ).onSuccess(rowSet -> {
      logger.info("Organization '{}' and associated data were successfully deleted", id);
      asyncResultHandler.handle(ResponseUtils.buildNoContentResponse());
    })
    .onFailure(throwable -> {
      logger.error("Failed to delete organization '{}' or associated data", id, throwable);
      asyncResultHandler.handle(ResponseUtils.buildErrorResponse(throwable));
    });
  }
}
