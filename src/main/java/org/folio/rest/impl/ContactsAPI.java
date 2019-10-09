package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Contact;
import org.folio.rest.jaxrs.model.ContactCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStorageContacts;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class ContactsAPI implements OrganizationsStorageContacts {
  private static final String CONTACT_TABLE = "contacts";

  @Override
  @Validate
  public void getOrganizationsStorageContacts(String query, int offset, int limit, String lang, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.get(CONTACT_TABLE, Contact.class, ContactCollection.class, query, offset, limit, okapiHeaders, vertxContext,
        GetOrganizationsStorageContactsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStorageContacts(String lang, org.folio.rest.jaxrs.model.Contact entity,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.post(CONTACT_TABLE, entity, okapiHeaders, vertxContext, PostOrganizationsStorageContactsResponse.class,
        asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStorageContactsById(String id, String lang, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.getById(CONTACT_TABLE, Contact.class, id, okapiHeaders, vertxContext, GetOrganizationsStorageContactsByIdResponse.class,
        asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStorageContactsById(String id, String lang, Map<String, String> okapiHeaders,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.deleteById(CONTACT_TABLE, id, okapiHeaders, vertxContext, DeleteOrganizationsStorageContactsByIdResponse.class,
        asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStorageContactsById(String id, String lang, org.folio.rest.jaxrs.model.Contact entity,
      Map<String, String> okapiHeaders, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) {
    PgUtil.put(CONTACT_TABLE, entity, id, okapiHeaders, vertxContext, PutOrganizationsStorageContactsByIdResponse.class,
        asyncResultHandler);
  }
}
