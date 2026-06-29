package org.folio.service.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.UUID;

import org.folio.rest.jaxrs.model.Metadata;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationAuditEvent;
import org.junit.jupiter.api.Test;

class AuditEventProducerTest {

  private final AuditEventProducer producer = new AuditEventProducer(null);

  @Test
  void organizationEditEventCarriesOriginalSnapshot() {
    var original = organization("Vendor Old", Organization.Status.ACTIVE);
    var updated = organization("Vendor New", Organization.Status.INACTIVE);

    OrganizationAuditEvent event = producer.getAuditEvent(updated, original, OrganizationAuditEvent.Action.EDIT);

    assertEquals(OrganizationAuditEvent.Action.EDIT, event.getAction());
    assertNotNull(event.getOrganizationSnapshot());
    assertEquals("Vendor New", event.getOrganizationSnapshot().getName());
    assertEquals(Organization.Status.INACTIVE, event.getOrganizationSnapshot().getStatus());

    assertNotNull(event.getOriginalOrganizationSnapshot());
    assertEquals("Vendor Old", event.getOriginalOrganizationSnapshot().getName());
    assertEquals(Organization.Status.ACTIVE, event.getOriginalOrganizationSnapshot().getStatus());
  }

  @Test
  void organizationCreateEventOmitsOriginalSnapshot() {
    OrganizationAuditEvent event = producer.getAuditEvent(organization("Vendor", Organization.Status.ACTIVE), null, OrganizationAuditEvent.Action.CREATE);

    assertEquals(OrganizationAuditEvent.Action.CREATE, event.getAction());
    assertNotNull(event.getOrganizationSnapshot());
    assertNull(event.getOriginalOrganizationSnapshot());
  }

  @Test
  void metadataIsStrippedFromBothSnapshots() {
    var original = organization("Vendor Old", Organization.Status.ACTIVE);
    var updated = organization("Vendor New", Organization.Status.INACTIVE);

    OrganizationAuditEvent event = producer.getAuditEvent(updated, original, OrganizationAuditEvent.Action.EDIT);

    assertNull(event.getOrganizationSnapshot().getMetadata());
    assertNull(event.getOriginalOrganizationSnapshot().getMetadata());
  }

  @Test
  void auditEventFieldsArePopulatedFromOrganizationMetadata() {
    var org = organization("Vendor", Organization.Status.ACTIVE);
    var expectedUserId = org.getMetadata().getUpdatedByUserId();
    var expectedActionDate = org.getMetadata().getUpdatedDate();

    OrganizationAuditEvent event = producer.getAuditEvent(org, null, OrganizationAuditEvent.Action.CREATE);

    assertEquals(org.getId(), event.getOrganizationId());
    assertEquals(expectedUserId, event.getUserId());
    assertEquals(expectedActionDate, event.getActionDate());
    assertNotNull(event.getId());
    assertNotNull(event.getEventDate());
  }

  private Organization organization(String name, Organization.Status status) {
    return new Organization()
      .withId(UUID.randomUUID().toString())
      .withName(name)
      .withStatus(status)
      .withMetadata(new Metadata()
        .withUpdatedDate(new Date())
        .withUpdatedByUserId(UUID.randomUUID().toString()));
  }

}
