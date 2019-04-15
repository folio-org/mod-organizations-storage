package org.folio.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Contact;
import org.folio.rest.jaxrs.model.ContactCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageContacts;
import org.folio.rest.persist.EntitiesMetadataHolder;
import org.folio.rest.persist.PgUtil;
import org.folio.rest.persist.PostgresClient;
import org.folio.rest.persist.QueryHolder;

import javax.ws.rs.core.Response;
import java.util.Map;

import static org.folio.rest.persist.HelperUtils.getEntitiesCollection;

public class ContactsAPI implements OrganizationsStorageContacts {
  private static final String CONTACT_TABLE = "contacts";

  private String idFieldName = "id";

  public ContactsAPI(Vertx vertx, String tenantId) {
    PostgresClient.getInstance(vertx, tenantId).setIdField(idFieldName);
  }


  @Override
  @Validate
  public void getOrganizationsStorageContacts(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    vertxContext.runOnContext((Void v) -> {
      EntitiesMetadataHolder<Contact, ContactCollection> entitiesMetadataHolder = new EntitiesMetadataHolder<>(Contact.class, ContactCollection.class, GetOrganizationsStorageContactsResponse.class);
      QueryHolder cql = new QueryHolder(CONTACT_TABLE, query, offset, limit);
      getEntitiesCollection(entitiesMetadataHolder, cql, asyncResultHandler, vertxContext, okapiHeaders);
    });
  }

  @Override
  @Validate
  public void postOrganizationsStorageContacts(String lang, org.folio.rest.jaxrs.model.Contact entity,
                                          Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(CONTACT_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageContactsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageContactsById(String id, String lang, Map<String, String> okapiHeaders,
                                             Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(CONTACT_TABLE, Contact.class, id, okapiHeaders,vertxContext, GetOrganizationsStorageContactsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageContactsById(String id, String lang, Map<String, String> okapiHeaders,
                                                Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(CONTACT_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStorageContactsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageContactsById(String id, String lang, org.folio.rest.jaxrs.model.Contact entity,
                                             Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(CONTACT_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStorageContactsByIdResponse.class, asyncResultHandler);
  }
}
