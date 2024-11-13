package org.folio.rest.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RestConstants {

  OKAPI_URL("x-okapi-url"),
  ORGANIZATION_PREFIX("/organizations-storage/organizations");

  private final String value;

}
