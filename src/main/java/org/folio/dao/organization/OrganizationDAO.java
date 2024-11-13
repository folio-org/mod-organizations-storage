package org.folio.dao.organization;

import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.persist.Conn;

import io.vertx.core.Future;

public interface OrganizationDAO {

  Future<String> createOrganization(Organization organization, Conn conn);
  Future<Void> updateOrganization(String id, Organization organization, Conn conn);
  Future<Void> deleteOrganization(String id, Conn conn);

}
