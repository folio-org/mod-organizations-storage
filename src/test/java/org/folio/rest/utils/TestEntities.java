package org.folio.rest.utils;

import org.folio.rest.jaxrs.model.Category;
import org.folio.rest.jaxrs.model.Contact;
import org.folio.rest.jaxrs.model.Interface;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationType;

public enum TestEntities {
  CATEGORY("/organizations-storage/categories", Category.class, "category.sample", "value", "AccountingServices", 4, true),
  CONTACT("/organizations-storage/contacts", Contact.class, "contact.sample", "notes", "ABC123", 13, false),
  PRIVILEGED_CONTACT("/organizations-storage/privileged-contacts", Contact.class, "contact.sample", "notes", "ABC123", 2, false),
  INTERFACE("/organizations-storage/interfaces", Interface.class, "interface.sample", "name", "Test Portal", 13, false),
  ORGANIZATION("/organizations-storage/organizations", Organization.class, "organization.sample", "code", "ABC123", 23, false),
  ORGANIZATION_TYPE("/organizations-storage/organization-types", OrganizationType.class, "organization_type.sample", "status", "Inactive", 4, true);

  private final int initialQuantity;
  private final String endpoint;
  private final String sampleFileName;
  private final String updatedFieldName;
  private final Object updatedFieldValue;
  private final boolean isReferenceData;
  private final Class<?> clazz;

  TestEntities(String endpoint, Class<?> clazz, String sampleFileName, String updatedFieldName, String updatedFieldValue, int initialQuantity, boolean isReferenceData) {
    this.endpoint = endpoint;
    this.clazz = clazz;
    this.sampleFileName = sampleFileName;
    this.updatedFieldName = updatedFieldName;
    this.updatedFieldValue = updatedFieldValue;
    this.initialQuantity = initialQuantity;
    this.isReferenceData = isReferenceData;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public String getEndpointWithId() {
    return endpoint + "/{id}";
  }

  public String getSampleFileName() {
    return sampleFileName;
  }

  public String getUpdatedFieldName() {
    return updatedFieldName;
  }

  public Object getUpdatedFieldValue() {
    return updatedFieldValue;
  }

  public int getInitialQuantity() {
    return initialQuantity;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  public boolean isReferenceData() {
    return isReferenceData;
  }
}
