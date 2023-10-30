package org.folio.rest.impl;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.folio.rest.annotations.Validate;
import org.folio.rest.jaxrs.model.Contact;
import org.folio.rest.jaxrs.model.ContactCollection;
import org.folio.rest.jaxrs.resource.OrganizationsStoragePrivilegedContacts;
import org.folio.rest.persist.PgUtil;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

public class PrivilegedContactsAPI implements OrganizationsStoragePrivilegedContacts {

  private static final String PRIVILEGED_CONTACTS_TABLE = "privileged_contacts";

  @Override
  @Validate
  public void getOrganizationsStoragePrivilegedContacts(String query,
                                                        String totalRecords,
                                                        int offset,
                                                        int limit, Map<String, String> okapiHeaders,
                                                        Handler<AsyncResult<Response>> asyncResultHandler,
                                                        Context vertxContext) {
    PgUtil.get(PRIVILEGED_CONTACTS_TABLE, Contact.class, ContactCollection.class, query, offset, limit, okapiHeaders, vertxContext,
      GetOrganizationsStoragePrivilegedContactsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void postOrganizationsStoragePrivilegedContacts(Contact entity,
                                                         Map<String, String> okapiHeaders,
                                                         Handler<AsyncResult<Response>> asyncResultHandler,
                                                         Context vertxContext) {
    PgUtil.post(PRIVILEGED_CONTACTS_TABLE, entity, okapiHeaders, vertxContext,
      PostOrganizationsStoragePrivilegedContactsResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void getOrganizationsStoragePrivilegedContactsById(String id,
                                                            Map<String, String> okapiHeaders,
                                                            Handler<AsyncResult<Response>> asyncResultHandler,
                                                            Context vertxContext) {
    PgUtil.getById(PRIVILEGED_CONTACTS_TABLE, Contact.class, id, okapiHeaders, vertxContext,
      GetOrganizationsStoragePrivilegedContactsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void deleteOrganizationsStoragePrivilegedContactsById(String id,
                                                               Map<String, String> okapiHeaders,
                                                               Handler<AsyncResult<Response>> asyncResultHandler,
                                                               Context vertxContext) {
    PgUtil.deleteById(PRIVILEGED_CONTACTS_TABLE, id, okapiHeaders, vertxContext,
      DeleteOrganizationsStoragePrivilegedContactsByIdResponse.class, asyncResultHandler);
  }

  @Override
  @Validate
  public void putOrganizationsStoragePrivilegedContactsById(String id, Contact entity,
                                                            Map<String, String> okapiHeaders,
                                                            Handler<AsyncResult<Response>> asyncResultHandler,
                                                            Context vertxContext) {
    PgUtil.put(PRIVILEGED_CONTACTS_TABLE, entity, id, okapiHeaders, vertxContext,
      PutOrganizationsStoragePrivilegedContactsByIdResponse.class, asyncResultHandler);
  }

}
