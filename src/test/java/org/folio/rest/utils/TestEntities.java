package org.folio.rest.utils;

import org.folio.rest.jaxrs.model.Address;
import org.folio.rest.jaxrs.model.Category;
import org.folio.rest.jaxrs.model.Contact;
import org.folio.rest.jaxrs.model.Email;
import org.folio.rest.jaxrs.model.Interface;
import org.folio.rest.jaxrs.model.Organization;
import org.folio.rest.jaxrs.model.PhoneNumber;
import org.folio.rest.jaxrs.model.Url;

public enum TestEntities {
  ADDRESS("/organization-storage/addresses", Address.class, "address.sample", "city", "Boston ", 0),
  CATEGORY("/organization-storage/categories", Category.class, "category.sample", "value", "AccountingServices", 4),
  CONTACT("/organization-storage/contacts", Contact.class, "contact.sample",  "notes", "ABC123", 13),
  EMAIL("/organization-storage/emails", Email.class, "email.sample", "value", "test@folio.org", 0),
  INTERFACE("/organization-storage/interfaces", Interface.class, "interface.sample", "name", "Test Portal", 0),
  ORGANIZATION("/organization-storage/organizations", Organization.class, "organization.sample",  "code", "ABC123", 13),
  PHONE_NUMBER("/organization-storage/phone-numbers", PhoneNumber.class, "phoneNumber.sample", "phone_number", "9999999999", 0),
  URL("/organization-storage/urls", Url.class, "url.sample", "value", "http://test.org", 0);


  TestEntities(String endpoint, Class<?> clazz, String sampleFileName, String updatedFieldName, String updatedFieldValue, int initialQuantity) {
    this.endpoint = endpoint;
    this.clazz = clazz;
    this.sampleFileName = sampleFileName;
    this.updatedFieldName = updatedFieldName;
    this.updatedFieldValue = updatedFieldValue;
    this.initialQuantity = initialQuantity;
  }

  private int initialQuantity;
  private String endpoint;
  private String sampleFileName;
  private String updatedFieldName;
  private String updatedFieldValue;
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

  public String getUpdatedFieldValue() {
    return updatedFieldValue;
  }

  public int getInitialQuantity() {
    return initialQuantity;
  }

  public Class<?> getClazz() {
    return clazz;
  }
}
