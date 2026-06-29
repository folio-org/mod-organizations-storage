package org.folio.service.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.folio.rest.jaxrs.model.Organization;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.Json;

class AuditOutboxServiceTest {

  private final AuditOutboxService service = new AuditOutboxService(null, null);

  @Test
  void decodesWrapperPayloadWithBothEntities() {
    var current = organization("Vendor New");
    var original = organization("Vendor Old");
    var payload = Json.encode(AuditEntityWrapper.of(current, original));

    AuditEntityWrapper<Organization> result = service.decodePayload(payload, Organization.class);

    assertNotNull(result.getEntity());
    assertEquals("Vendor New", result.getEntity().getName());
    assertNotNull(result.getOriginalEntity());
    assertEquals("Vendor Old", result.getOriginalEntity().getName());
  }

  @Test
  void decodesWrapperPayloadWithNullOriginal() {
    var current = organization("Vendor");
    var payload = Json.encode(AuditEntityWrapper.of(current, null));

    AuditEntityWrapper<Organization> result = service.decodePayload(payload, Organization.class);

    assertEquals("Vendor", result.getEntity().getName());
    assertNull(result.getOriginalEntity());
  }

  @Test
  void fallsBackToBareEntityForLegacyPayload() {
    var current = organization("Legacy Vendor");
    var legacyPayload = Json.encode(current);

    AuditEntityWrapper<Organization> result = service.decodePayload(legacyPayload, Organization.class);

    assertNotNull(result.getEntity());
    assertEquals("Legacy Vendor", result.getEntity().getName());
    assertNull(result.getOriginalEntity());
  }

  private Organization organization(String name) {
    return new Organization()
      .withId(UUID.randomUUID().toString())
      .withName(name);
  }

}
