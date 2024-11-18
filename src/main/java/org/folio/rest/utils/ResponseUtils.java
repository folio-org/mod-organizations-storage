package org.folio.rest.utils;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import javax.ws.rs.core.Response;

import java.net.URI;
import java.net.URISyntaxException;

import org.folio.HttpStatus;
import org.folio.rest.persist.PgExceptionUtil;

import io.vertx.core.Future;
import io.vertx.ext.web.handler.HttpException;

public class ResponseUtils {

  public static Future<Response> buildNoContentResponse() {
    return Future.succeededFuture(Response.noContent().build());
  }

  public static Future<Response> buildResponseWithLocation(String okapi, String endpoint, Object body) {
    try {
      return Future.succeededFuture(
        Response.created(new URI(okapi + endpoint))
          .header(CONTENT_TYPE, APPLICATION_JSON)
          .entity(body)
          .build()
      );
    } catch (URISyntaxException e) {
      return Future.succeededFuture(
        Response.created(URI.create(endpoint))
          .header(CONTENT_TYPE, APPLICATION_JSON)
          .header(LOCATION, endpoint)
          .entity(body)
          .build()
      );
    }
  }

  public static Future<Response> buildBadRequestResponse(String body) {
    return buildErrorResponse(BAD_REQUEST.getStatusCode(), body);
  }

  public static Future<Response> buildErrorResponse(Throwable throwable) {
    if (throwable instanceof HttpException httpException) {
      return buildErrorResponse(httpException.getStatusCode(), httpException.getPayload());
    } else {
      return buildErrorResponse(INTERNAL_SERVER_ERROR.getStatusCode(), throwable.getMessage());
    }
  }

  private static Future<Response> buildErrorResponse(int code, String message) {
    return Future.succeededFuture(Response.status(code)
      .header(CONTENT_TYPE, TEXT_PLAIN)
      .entity(message)
      .build());
  }

  public static Throwable convertPgExceptionIfNeeded(Throwable cause, HttpStatus status) {
    var badRequestMessage = PgExceptionUtil.badRequestMessage(cause);
    if (badRequestMessage != null) {
      return new HttpException(status.toInt(), badRequestMessage);
    } else {
      return new HttpException(INTERNAL_SERVER_ERROR.getStatusCode(), cause.getMessage());
    }
  }

  private ResponseUtils() {}

}
