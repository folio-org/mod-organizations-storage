package org.folio.dao.organization;

import static org.folio.rest.utils.ResponseUtils.convertPgExceptionIfNeeded;

import java.util.UUID;

import org.folio.HttpStatus;
import org.folio.dao.DbUtils;
import org.folio.persist.CriterionBuilder;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.persist.Conn;
import org.folio.rest.persist.Criteria.Criterion;

import io.vertx.core.Future;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class OrganizationPostgresDAO implements OrganizationDAO {

  private static final String ORGANIZATION_TABLE = "organizations";
  private static final String BANKING_INFORMATION_TABLE = "banking_information";

  @Override
  public Future<String> createOrganization(Organization organization, Conn conn) {
    log.info("createOrganization:: Creating new organization with id: '{}'", organization.getId());
    if (organization.getId() == null) {
      organization.setId(UUID.randomUUID().toString());
    }
    return conn.save(ORGANIZATION_TABLE, organization.getId(), organization, true)
      .recover(t -> Future.failedFuture(convertPgExceptionIfNeeded(t, HttpStatus.HTTP_BAD_REQUEST)))
      .onSuccess(s -> log.info("createOrganization:: New organization with id: '{}' successfully created", organization.getId()))
      .onFailure(t -> log.error("Failed to create organization with id: '{}'", organization.getId(), t));
  }

  @Override
  public Future<Void> updateOrganization(String id, Organization organization, Conn conn) {
    return conn.update(ORGANIZATION_TABLE, organization, id)
      .recover(t -> Future.failedFuture(convertPgExceptionIfNeeded(t, HttpStatus.HTTP_UNPROCESSABLE_ENTITY)))
      .compose(DbUtils::verifyEntityUpdate)
      .onSuccess(v -> log.info("updateOrganization:: Organization with id: '{}' successfully updated", organization.getId()))
      .onFailure(t -> log.error("Update failed for organization with id: '{}'", organization.getId(), t))
      .mapEmpty();
  }

  @Override
  public Future<Void> deleteOrganization(String id, Conn conn) {
    log.info("deleteOrganization:: Deleting organization with id: '{}'", id);
    Criterion criterion = new CriterionBuilder().with("organizationId", id).build();
    return conn.delete(BANKING_INFORMATION_TABLE, criterion)
      .compose(res -> {
        log.info("deleteOrganization:: Deleted {} records from table: '{}'", res.rowCount(), BANKING_INFORMATION_TABLE);
        return conn.delete(ORGANIZATION_TABLE, id);
      }).compose(DbUtils::verifyEntityUpdate);
  }

}
