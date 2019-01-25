package org.folio.rest.impl;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.RestVerticle;
import org.folio.rest.jaxrs.model.EdiFtp;
import org.folio.rest.jaxrs.model.EdiFtpCollection;
import org.folio.rest.jaxrs.resource.VendorStorageEdiFtps;
import org.folio.rest.persist.Criteria.Criteria;
import org.folio.rest.persist.Criteria.Criterion;
import org.folio.rest.persist.Criteria.Limit;
import org.folio.rest.persist.Criteria.Offset;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.cql.CQLWrapper;
import org.folio.rest.tools.messages.MessageConsts;
import org.folio.rest.tools.messages.Messages;
import org.folio.rest.tools.utils.OutStream;
import org.folio.rest.tools.utils.TenantTool;
import org.z3950.zing.cql.cql2pgjson.CQL2PgJSON;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EdiFtpsAPI implements VendorStorageEdiFtps {
  private static final String EDI_FTP_TABLE = "edi_ftp";
  private static final String EDI_FTP_LOCATION_PREFIX = "/vendor-storage/edi-ftps/";

  private static final Logger log = LoggerFactory.getLogger(EdiFtpsAPI.class);
  private final Messages messages = Messages.getInstance();
  private String idFieldName = "id";

  private static void respond(Handler<AsyncResult<Response>> handler, Response response) {
    AsyncResult<Response> result = Future.succeededFuture(response);
    handler.handle(result);
  }

  private boolean isInvalidUUID (String errorMessage) {
    return (errorMessage != null && errorMessage.contains("invalid input syntax for uuid"));
  }

  public EdiFtpsAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getVendorStorageEdiFtps(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      try {
        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );

        String[] fieldList = {"*"};
        CQL2PgJSON cql2PgJSON = new CQL2PgJSON(String.format("%s.jsonb", EDI_FTP_TABLE));
        CQLWrapper cql = new CQLWrapper(cql2PgJSON, query)
          .setLimit(new Limit(limit))
          .setOffset(new Offset(offset));

        PostgresClient.getInstance(vertxContext.owner(), tenantId).get(EDI_FTP_TABLE, EdiFtp.class, fieldList, cql,
          true, false, reply -> {
            try {
              if(reply.succeeded()){
                EdiFtpCollection collection = new EdiFtpCollection();
                @SuppressWarnings("unchecked")
                List<EdiFtp> results = (List<EdiFtp>)reply.result().getResults();
                collection.setEdiFtps(results);
                Integer totalRecords = reply.result().getResultInfo().getTotalRecords();
                collection.setTotalRecords(totalRecords);
                Integer first = 0;
                Integer last = 0;
                if (!results.isEmpty()) {
                  first = offset + 1;
                  last = offset + results.size();
                }
                collection.setFirst(first);
                collection.setLast(last);
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsResponse
                  .respond200WithApplicationJson(collection)));
              }
              else{
                log.error(reply.cause().getMessage(), reply.cause());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsResponse
                  .respond400WithTextPlain(reply.cause().getMessage())));
              }
            } catch (Exception e) {
              log.error(e.getMessage(), e);
              asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsResponse
                .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
            }
          });
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        String message = messages.getMessage(lang, MessageConsts.InternalServerError);
        if(e.getCause() != null && e.getCause().getClass().getSimpleName().endsWith("CQLParseException")){
          message = " CQL parse error " + e.getLocalizedMessage();
        }
        asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsResponse
          .respond500WithTextPlain(message)));
      }
    });
  }

  @Override
  public void postVendorStorageEdiFtps(String lang, EdiFtp entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {

      try {
        String id = UUID.randomUUID().toString();
        if(entity.getId() == null){
          entity.setId(id);
        }
        else{
          id = entity.getId();
        }

        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );
        PostgresClient.getInstance(vertxContext.owner(), tenantId).save(
          EDI_FTP_TABLE, id, entity,
          reply -> {
            try {
              if (reply.succeeded()) {
                String persistenceId = reply.result();
                entity.setId(persistenceId);
                OutStream stream = new OutStream();
                stream.setData(entity);

                Response response = VendorStorageEdiFtps.PostVendorStorageEdiFtpsResponse.respond201WithApplicationJson(stream,
                  VendorStorageEdiFtps.PostVendorStorageEdiFtpsResponse.headersFor201()
                    .withLocation(EDI_FTP_LOCATION_PREFIX + persistenceId));
                respond(asyncResultHandler, response);
              }
              else {
                log.error(reply.cause().getMessage(), reply.cause());
                Response response = VendorStorageEdiFtps.PostVendorStorageEdiFtpsResponse
                  .respond500WithTextPlain(reply.cause().getMessage());
                respond(asyncResultHandler, response);
              }
            }
            catch (Exception e) {
              log.error(e.getMessage(), e);

              Response response = VendorStorageEdiFtps.PostVendorStorageEdiFtpsResponse.respond500WithTextPlain(e.getMessage());
              respond(asyncResultHandler, response);
            }

          }
        );
      }
      catch (Exception e) {
        log.error(e.getMessage(), e);

        String errMsg = messages.getMessage(lang, MessageConsts.InternalServerError);
        Response response = VendorStorageEdiFtps.PostVendorStorageEdiFtpsResponse.respond500WithTextPlain(errMsg);
        respond(asyncResultHandler, response);
      }

    });
  }

  @Override
  public void getVendorStorageEdiFtpsById(String ediFtpId, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      try {
        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );

        String idArgument = String.format("'%s'", ediFtpId);
        Criterion c = new Criterion(
          new Criteria().addField(idFieldName).setJSONB(false).setOperation("=").setValue(idArgument));

        PostgresClient.getInstance(vertxContext.owner(), tenantId).get(EDI_FTP_TABLE, EdiFtp.class, c, true,
          reply -> {
            try {
              if (reply.succeeded()) {
                List<EdiFtp> results = (List<EdiFtp>) reply.result().getResults();
                if (results.isEmpty()) {
                  asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsByIdResponse
                    .respond404WithTextPlain(ediFtpId)));
                }
                else{
                  asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsByIdResponse
                    .respond200WithApplicationJson(results.get(0))));
                }
              }
              else{
                log.error(reply.cause().getMessage(), reply.cause());
                if (isInvalidUUID(reply.cause().getMessage())) {
                  asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsByIdResponse
                    .respond404WithTextPlain(ediFtpId)));
                }
                else{
                  asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsByIdResponse
                    .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
                }
              }
            } catch (Exception e) {
              log.error(e.getMessage(), e);
              asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsByIdResponse
                .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
            }
          });
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.GetVendorStorageEdiFtpsByIdResponse
          .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
      }
    });
  }

  @Override
  public void deleteVendorStorageEdiFtpsById(String ediFtpId, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    String tenantId = TenantTool.tenantId(okapiHeaders);

    try {
      vertxContext.runOnContext(v -> {
        PostgresClient postgresClient = PostgresClient.getInstance(
          vertxContext.owner(), TenantTool.calculateTenantId(tenantId));

        try {
          postgresClient.delete(EDI_FTP_TABLE, ediFtpId, reply -> {
            if (reply.succeeded()) {
              asyncResultHandler.handle(Future.succeededFuture(
                VendorStorageEdiFtps.DeleteVendorStorageEdiFtpsByIdResponse.noContent()
                  .build()));
            } else {
              asyncResultHandler.handle(Future.succeededFuture(
                VendorStorageEdiFtps.DeleteVendorStorageEdiFtpsByIdResponse.respond500WithTextPlain(reply.cause().getMessage())));
            }
          });
        } catch (Exception e) {
          asyncResultHandler.handle(Future.succeededFuture(
            VendorStorageEdiFtps.DeleteVendorStorageEdiFtpsByIdResponse.respond500WithTextPlain(e.getMessage())));
        }
      });
    }
    catch(Exception e) {
      asyncResultHandler.handle(Future.succeededFuture(
        VendorStorageEdiFtps.DeleteVendorStorageEdiFtpsByIdResponse.respond500WithTextPlain(e.getMessage())));
    }
  }

  @Override
  public void putVendorStorageEdiFtpsById(String ediFtpId, String lang, EdiFtp entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext(v -> {
      String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );
      try {
        if(entity.getId() == null){
          entity.setId(ediFtpId);
        }
        PostgresClient.getInstance(vertxContext.owner(), tenantId).update(
          EDI_FTP_TABLE, entity, ediFtpId,
          reply -> {
            try {
              if(reply.succeeded()){
                if (reply.result().getUpdated() == 0) {
                  asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.PutVendorStorageEdiFtpsByIdResponse
                    .respond404WithTextPlain(messages.getMessage(lang, MessageConsts.NoRecordsUpdated))));
                }
                else{
                  asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.PutVendorStorageEdiFtpsByIdResponse
                    .respond204()));
                }
              }
              else{
                log.error(reply.cause().getMessage());
                asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.PutVendorStorageEdiFtpsByIdResponse
                  .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
              }
            } catch (Exception e) {
              log.error(e.getMessage(), e);
              asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.PutVendorStorageEdiFtpsByIdResponse
                .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
            }
          });
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        asyncResultHandler.handle(Future.succeededFuture(VendorStorageEdiFtps.PutVendorStorageEdiFtpsByIdResponse
          .respond500WithTextPlain(messages.getMessage(lang, MessageConsts.InternalServerError))));
      }
    });
  }
}