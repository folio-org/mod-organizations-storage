package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Contact;
import org.folio.rest.jaxrs.model.ContactCollection;
import org.folio.rest.jaxrs.resource.OrganizationStorageContacts;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

public class ContactsAPI implements OrganizationStorageContacts {
  private static final String CONTACT_TABLE = "contacts";

  private String idFieldName = "id";

  public ContactsAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  public void getOrganizationStorageContacts(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      EntitiesMetadataHolder<Contact, ContactCollection> entitiesMetadataHolder = new EntitiesMetadataHolder<>(Contact.class, ContactCollection.class, GetOrganizationStorageContactsResponse.class);
      QueryHolder cql = new QueryHolder(CONTACT_TABLE, query, offset, limit);
      getEntitiesCollection(entitiesMetadataHolder, cql, asyncResultHandler, vertxContext, okapiHeaders);
    });
  }

  @Override
  @Validate
  public void postOrganizationStorageContacts(String lang, org.folio.rest.jaxrs.model.Contact entity,
                                          Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(CONTACT_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationStorageContactsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationStorageContactsById(String id, String lang, Map<String, String> okapiHeaders,
                                             Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(CONTACT_TABLE, Contact.class, id, okapiHeaders,vertxContext, GetOrganizationStorageContactsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationStorageContactsById(String id, String lang, Map<String, String> okapiHeaders,
                                                Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(CONTACT_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationStorageContactsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationStorageContactsById(String id, String lang, org.folio.rest.jaxrs.model.Contact entity,
                                             Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(CONTACT_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationStorageContactsByIdResponse.class, asyncResultHandler);
  }
}
