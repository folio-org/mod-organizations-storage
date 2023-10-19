package org.folio.util;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.folio.rest.persist.Conn;
import org.folio.rest.persist.PgExceptionUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.ext.web.handler.HttpException;

public class ResponseUtils {

  private static final Logger logger = LogManager.getLogger(ResponseUtils.class);

  private ResponseUtils() {
  }

  public static void handleNoContentResponse(AsyncResult<Conn> result,
                                             String id,
                                             String logMessage,
                                             Handler<AsyncResult<Response>> onComplete) {
    logger.debug("handleNoContentResponse:: Trying to handle no content response for entity: {}', logMessage: {}", id, logMessage);
    if (result.failed()) {
      HttpException cause = (HttpException) result.cause();
      logger.error(logMessage, cause, id, "or associated data failed to be");
      onComplete.handle(buildErrorResponse(cause));
    } else {
      logger.info(logMessage, id, "and associated data were successfully");
      onComplete.handle(buildNoContentResponse());
    }
  }

  public static void handleFailure(Promise<?> promise, AsyncResult<?> reply) {
    Throwable cause = reply.cause();
    String badRequestMessage = PgExceptionUtil.badRequestMessage(cause);
    if (badRequestMessage != null) {
      promise.fail(new HttpException(Response.Status.BAD_REQUEST.getStatusCode(), badRequestMessage));
    } else {
      promise.fail(new HttpException(INTERNAL_SERVER_ERROR.getStatusCode(), cause.getMessage()));
    }
  }

  private static Future<Response> buildNoContentResponse() {
    return Future.succeededFuture(Response.noContent().build());
  }

  private static Future<Response> buildErrorResponse(Throwable throwable) {
    final String message;
    final int code;

    if (throwable instanceof HttpException) {
      code = ((HttpException) throwable).getStatusCode();
      message = ((HttpException) throwable).getPayload();
    } else {
      code = INTERNAL_SERVER_ERROR.getStatusCode();
      message = throwable.getMessage();
    }

    return Future.succeededFuture(buildErrorResponse(code, message));
  }

  private static Response buildErrorResponse(int code, String message) {
    return Response.status(code)
      .header(CONTENT_TYPE, TEXT_PLAIN)
      .entity(message)
      .build();
  }

}
