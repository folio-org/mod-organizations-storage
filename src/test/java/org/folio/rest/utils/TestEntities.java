package org.folio.rest.utils;

import org.folio.rest.jaxrs.model.Address;
import org.folio.rest.jaxrs.model.Category;
import org.folio.rest.jaxrs.model.Contact;
import org.folio.rest.jaxrs.model.Email;
import org.folio.rest.jaxrs.model.Interface;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.OrganizationType;
import org.folio.rest.jaxrs.model.PhoneNumber;
import org.folio.rest.jaxrs.model.Url;

public enum TestEntities {
  ADDRESS("/organizations-storage/addresses", Address.class, "address.sample", "city", "Boston ", 0, false),
  CATEGORY("/organizations-storage/categories", Category.class, "category.sample", "value", "AccountingServices", 4, true),
  CONTACT("/organizations-storage/contacts", Contact.class, "contact.sample", "notes", "ABC123", 13, false),
  PRIVILEGED_CONTACT("/organizations-storage/privileged-contacts", Contact.class, "contact.sample", "notes", "ABC123", 0, false),
  EMAIL("/organizations-storage/emails", Email.class, "email.sample", "value", "test@folio.org", 0, false),
  INTERFACE("/organizations-storage/interfaces", Interface.class, "interface.sample", "name", "Test Portal", 13, false),
  ORGANIZATION("/organizations-storage/organizations", Organization.class, "organization.sample", "code", "ABC123", 16, false),
  ORGANIZATION_TYPE("/organizations-storage/organization-types", OrganizationType.class, "organization_type.sample", "status", "Inactive", 4, true),
  PHONE_NUMBER("/organizations-storage/phone-numbers", PhoneNumber.class, "phoneNumber.sample", "phoneNumber", "9999999999", 0, false),
  URL("/organizations-storage/urls", Url.class, "url.sample", "value", "http://test.org", 0, false);


  TestEntities(String endpoint, Class<?> clazz, String sampleFileName, String updatedFieldName, String updatedFieldValue, int initialQuantity, boolean isReferenceData) {
    this.endpoint = endpoint;
    this.clazz = clazz;
    this.sampleFileName = sampleFileName;
    this.updatedFieldName = updatedFieldName;
    this.updatedFieldValue = updatedFieldValue;
    this.initialQuantity = initialQuantity;
    this.isReferenceData = isReferenceData;
  }

  private int initialQuantity;
  private String endpoint;
  private String sampleFileName;
  private String updatedFieldName;
  private Object updatedFieldValue;
  private boolean isReferenceData;
  private Class<?> clazz;

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
