package org.folio.dao;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.folio.rest.persist.PostgresClient.convertToPsqlStandard;

import java.util.Map;

import org.folio.rest.persist.PostgresClient;
import org.folio.rest.tools.utils.TenantTool;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.ext.web.handler.HttpException;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class DbUtils {

  private static final String TABLE_NAME_TEMPLATE = "%s.%s";

  public static String getTenantTableName(String tenantId, String tableName) {
    return TABLE_NAME_TEMPLATE.formatted(convertToPsqlStandard(tenantId), tableName);
  }

  public static Future<Void> verifyEntityUpdate(RowSet<Row> updated) {
    return updated.rowCount() == 1
      ? Future.succeededFuture()
      : Future.failedFuture(new HttpException(NOT_FOUND.getStatusCode(), NOT_FOUND.getReasonPhrase()));
  }

  public static PostgresClient getPgClient(Context vertxContext, Map<String, String> okapiHeaders) {
    return getPgClient(vertxContext, TenantTool.tenantId(okapiHeaders));
  }

  public static PostgresClient getPgClient(Context vertxContext, String tenantId) {
    if (PostgresClient.DEFAULT_SCHEMA.equals(tenantId)) {
      return PostgresClient.getInstance(vertxContext.owner());
    }
    return PostgresClient.getInstance(vertxContext.owner(), tenantId);
  }

  private DbUtils() {}

}
