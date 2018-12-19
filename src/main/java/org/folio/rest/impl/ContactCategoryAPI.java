package org.folio.rest.impl;

import io.vertx.core.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.folio.rest.RestVerticle;
import org.folio.rest.jaxrs.model.Category;
//import org.folio.rest.jaxrs.model.ContactCategoryCollection;
//import org.folio.rest.jaxrs.resource.ContactCategoryResource;
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

public class ContactCategoryAPI  {
//  private static final String CONTACT_CATEGORY_TABLE = "contact_category";
//  private static final String CONTACT_CATEGORY_LOCATION_PREFIX = "/contact_category/";
//
//  private static final Logger log = LoggerFactory.getLogger(ContactCategoryAPI.class);
//  private final Messages messages = Messages.getInstance();
//  private String idFieldName = "id";
//
//  private static void respond(Handler<AsyncResult<Response>> handler, Response response) {
//    AsyncResult<Response> result = Future.succeededFuture(response);
//    handler.handle(result);
//  }
//
//  private boolean isInvalidUUID (String errorMessage) {
//    return (errorMessage != null && errorMessage.contains("invalid input syntax for uuid"));
//  }
//
//  public ContactCategoryAPI(Vertx vertx, String tenantId) {
//    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
//  }
//
//
//  @Override
//  public void getContactCategory(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
//    vertxContext.runOnContext((Void v) -> {
//      try {
//        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );
//
//        String[] fieldList = {"*"};
//        CQL2PgJSON cql2PgJSON = new CQL2PgJSON(String.format("%s.jsonb", CONTACT_CATEGORY_TABLE));
//        CQLWrapper cql = new CQLWrapper(cql2PgJSON, query)
//          .setLimit(new Limit(limit))
//          .setOffset(new Offset(offset));
//
//        PostgresClient.getInstance(vertxContext.owner(), tenantId).get(CONTACT_CATEGORY_TABLE, Category.class, fieldList, cql,
//          true, false, reply -> {
//            try {
//              if(reply.succeeded()){
//                ContactCategoryCollection collection = new ContactCategoryCollection();
//                @SuppressWarnings("unchecked")
//                List<Category> results = (List<Category>)reply.result().getResults();
//                collection.setCategories(results);
//                Integer totalRecords = reply.result().getResultInfo().getTotalRecords();
//                collection.setTotalRecords(totalRecords);
//                Integer first = 0;
//                Integer last = 0;
//                if (!results.isEmpty()) {
//                  first = offset + 1;
//                  last = offset + results.size();
//                }
//                collection.setFirst(first);
//                collection.setLast(last);
//                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(ContactCategoryResource.GetContactCategoryResponse
//                  .withJsonOK(collection)));
//              }
//              else{
//                log.error(reply.cause().getMessage(), reply.cause());
//                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(ContactCategoryResource.GetContactCategoryResponse
//                  .withPlainBadRequest(reply.cause().getMessage())));
//              }
//            } catch (Exception e) {
//              log.error(e.getMessage(), e);
//              asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(ContactCategoryResource.GetContactCategoryResponse
//                .withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError))));
//            }
//          });
//      } catch (Exception e) {
//        log.error(e.getMessage(), e);
//        String message = messages.getMessage(lang, MessageConsts.InternalServerError);
//        if(e.getCause() != null && e.getCause().getClass().getSimpleName().endsWith("CQLParseException")){
//          message = " CQL parse error " + e.getLocalizedMessage();
//        }
//        asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(ContactCategoryResource.GetContactCategoryResponse
//          .withPlainInternalServerError(message)));
//      }
//    });
//  }
//
//  @Override
//  public void postContactCategory(String lang, Category entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
//    vertxContext.runOnContext(v -> {
//
//      try {
//        String id = UUID.randomUUID().toString();
//        if(entity.getId() == null){
//          entity.setId(id);
//        }
//        else{
//          id = entity.getId();
//        }
//
//        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );
//        PostgresClient.getInstance(vertxContext.owner(), tenantId).save(
//          CONTACT_CATEGORY_TABLE, id, entity,
//          reply -> {
//            try {
//              if (reply.succeeded()) {
//                String persistenceId = reply.result();
//                entity.setId(persistenceId);
//                OutStream stream = new OutStream();
//                stream.setData(entity);
//
//                Response response = PostContactCategoryResponse.
//                  withJsonCreated(CONTACT_CATEGORY_LOCATION_PREFIX + persistenceId, stream);
//                respond(asyncResultHandler, response);
//              }
//              else {
//                log.error(reply.cause().getMessage(), reply.cause());
//                Response response = PostContactCategoryResponse.withPlainInternalServerError(reply.cause().getMessage());
//                respond(asyncResultHandler, response);
//              }
//            }
//            catch (Exception e) {
//              log.error(e.getMessage(), e);
//
//              Response response = PostContactCategoryResponse.withPlainInternalServerError(e.getMessage());
//              respond(asyncResultHandler, response);
//            }
//
//          }
//        );
//      }
//      catch (Exception e) {
//        log.error(e.getMessage(), e);
//
//        String errMsg = messages.getMessage(lang, MessageConsts.InternalServerError);
//        Response response = PostContactCategoryResponse.withPlainInternalServerError(errMsg);
//        respond(asyncResultHandler, response);
//      }
//
//    });
//  }
//
//  @Override
//  public void getContactCategoryById(String categoryId, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
//    vertxContext.runOnContext(v -> {
//      try {
//        String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );
//
//        String idArgument = String.format("'%s'", categoryId);
//        Criterion c = new Criterion(
//          new Criteria().addField(idFieldName).setJSONB(false).setOperation("=").setValue(idArgument));
//
//        PostgresClient.getInstance(vertxContext.owner(), tenantId).get(CONTACT_CATEGORY_TABLE, Category.class, c, true,
//          reply -> {
//            try {
//              if (reply.succeeded()) {
//                @SuppressWarnings("unchecked")
//                List<Category> results = (List<Category>) reply.result().getResults();
//                if (results.isEmpty()) {
//                  asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.GetContactCategoryByIdResponse
//                    .withPlainNotFound(categoryId)));
//                }
//                else{
//                  asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.GetContactCategoryByIdResponse
//                    .withJsonOK(results.get(0))));
//                }
//              }
//              else{
//                log.error(reply.cause().getMessage(), reply.cause());
//                if (isInvalidUUID(reply.cause().getMessage())) {
//                  asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.GetContactCategoryByIdResponse
//                    .withPlainNotFound(categoryId)));
//                }
//                else{
//                  asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.GetContactCategoryByIdResponse
//                    .withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError))));
//                }
//              }
//            } catch (Exception e) {
//              log.error(e.getMessage(), e);
//              asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.GetContactCategoryByIdResponse
//                .withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError))));
//            }
//          });
//      } catch (Exception e) {
//        log.error(e.getMessage(), e);
//        asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.GetContactCategoryByIdResponse
//          .withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError))));
//      }
//    });
//  }
//
//  @Override
//  public void deleteContactCategoryById(String categoryId, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
//    String tenantId = TenantTool.tenantId(okapiHeaders);
//
//    try {
//      vertxContext.runOnContext(v -> {
//        PostgresClient postgresClient = PostgresClient.getInstance(
//          vertxContext.owner(), TenantTool.calculateTenantId(tenantId));
//
//        try {
//          postgresClient.delete(CONTACT_CATEGORY_TABLE, categoryId, reply -> {
//            if (reply.succeeded()) {
//              asyncResultHandler.handle(Future.succeededFuture(
//                ContactCategoryAPI.DeleteContactCategoryByIdResponse.noContent()
//                  .build()));
//            } else {
//              asyncResultHandler.handle(Future.succeededFuture(
//                ContactCategoryAPI.DeleteContactCategoryByIdResponse.
//                  withPlainInternalServerError(reply.cause().getMessage())));
//            }
//          });
//        } catch (Exception e) {
//          asyncResultHandler.handle(Future.succeededFuture(
//            ContactCategoryAPI.DeleteContactCategoryByIdResponse.
//              withPlainInternalServerError(e.getMessage())));
//        }
//      });
//    }
//    catch(Exception e) {
//      asyncResultHandler.handle(Future.succeededFuture(
//        ContactCategoryAPI.DeleteContactCategoryByIdResponse.
//          withPlainInternalServerError(e.getMessage())));
//    }
//  }
//
//  @Override
//  public void putContactCategoryById(String categoryId, String lang, Category entity, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
//    vertxContext.runOnContext(v -> {
//      String tenantId = TenantTool.calculateTenantId( okapiHeaders.get(RestVerticle.OKAPI_HEADER_TENANT) );
//      try {
//        if(entity.getId() == null){
//          entity.setId(categoryId);
//        }
//        PostgresClient.getInstance(vertxContext.owner(), tenantId).update(
//          CONTACT_CATEGORY_TABLE, entity, categoryId,
//          reply -> {
//            try {
//              if(reply.succeeded()){
//                if (reply.result().getUpdated() == 0) {
//                  asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.PutContactCategoryByIdResponse
//                    .withPlainNotFound(messages.getMessage(lang, MessageConsts.NoRecordsUpdated))));
//                }
//                else{
//                  asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.PutContactCategoryByIdResponse
//                    .withNoContent()));
//                }
//              }
//              else{
//                log.error(reply.cause().getMessage());
//                asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.PutContactCategoryByIdResponse
//                  .withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError))));
//              }
//            } catch (Exception e) {
//              log.error(e.getMessage(), e);
//              asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.PutContactCategoryByIdResponse
//                .withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError))));
//            }
//          });
//      } catch (Exception e) {
//        log.error(e.getMessage(), e);
//        asyncResultHandler.handle(Future.succeededFuture(ContactCategoryAPI.PutContactCategoryByIdResponse
//          .withPlainInternalServerError(messages.getMessage(lang, MessageConsts.InternalServerError))));
//      }
//    });
//  }
}
