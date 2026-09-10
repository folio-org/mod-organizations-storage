package org.folio.service.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.folio.rest.jaxrs.model.Metadata;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationAuditEvent;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

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
  void metadataIsRetainedInBothSnapshots() {
    var original = organization("Vendor Old", Organization.Status.ACTIVE);
    var updated = organization("Vendor New", Organization.Status.INACTIVE);

    OrganizationAuditEvent event = producer.getAuditEvent(updated, original, OrganizationAuditEvent.Action.EDIT);

    assertEquals(updated.getMetadata(), event.getOrganizationSnapshot().getMetadata());
    assertEquals(original.getMetadata(), event.getOriginalOrganizationSnapshot().getMetadata());
  }

  @Test
  void editEventSnapshotCarriesOriginalCreationMetadata() {
    var creatorId = UUID.randomUUID().toString();
    var editorId = UUID.randomUUID().toString();
    var original = organization("Vendor Old", Organization.Status.ACTIVE)
      .withMetadata(new Metadata()
        .withCreatedDate(Date.from(Instant.parse("2024-11-04T12:25:22.868Z")))
        .withCreatedByUserId(creatorId)
        .withUpdatedDate(Date.from(Instant.parse("2024-11-04T12:25:22.868Z")))
        .withUpdatedByUserId(creatorId));
    var editDate = new Date();
    var updated = organization("Vendor New", Organization.Status.INACTIVE)
      .withMetadata(new Metadata()
        .withCreatedDate(editDate)
        .withCreatedByUserId(editorId)
        .withUpdatedDate(editDate)
        .withUpdatedByUserId(editorId));

    var event = new JsonObject(Json.encode(producer.getAuditEvent(updated, original, OrganizationAuditEvent.Action.EDIT)));

    JsonObject postMetadata = event.getJsonObject("organizationSnapshot").getJsonObject("metadata");
    JsonObject preMetadata = event.getJsonObject("originalOrganizationSnapshot").getJsonObject("metadata");
    assertNotNull(postMetadata, "Snapshot must keep the metadata the consumer needs");
    assertNotNull(preMetadata);
    // RMB stamps the PUT body with a createdDate of "now"; the event must report when the organization was really created
    assertEquals(preMetadata.getString("createdDate"), postMetadata.getString("createdDate"),
      "Post-edit snapshot must carry the original createdDate, not the edit timestamp");
    assertEquals(preMetadata.getString("createdByUserId"), postMetadata.getString("createdByUserId"));
    assertEquals(event.getString("actionDate"), postMetadata.getString("updatedDate"));
    assertEquals(editorId, postMetadata.getString("updatedByUserId"));
  }

  @Test
  void editEventSnapshotKeepsOwnCreationMetadataWhenOriginalHasNoMetadata() {
    var original = organization("Vendor Old", Organization.Status.ACTIVE).withMetadata(null);
    var updated = organization("Vendor New", Organization.Status.INACTIVE);
    var createdDate = new Date();
    var createdByUserId = UUID.randomUUID().toString();
    updated.getMetadata().withCreatedDate(createdDate).withCreatedByUserId(createdByUserId);

    OrganizationAuditEvent event = producer.getAuditEvent(updated, original, OrganizationAuditEvent.Action.EDIT);

    assertEquals(createdDate, event.getOrganizationSnapshot().getMetadata().getCreatedDate());
    assertEquals(createdByUserId, event.getOrganizationSnapshot().getMetadata().getCreatedByUserId());
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
