package org.folio.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.folio.CopilotGenerated;
import org.junit.jupiter.api.Test;

@CopilotGenerated(model = "Claude Sonnet 4.5")
class EnvUtilsTest {

  @Test
  void testGetEnvVarReturnsDefaultWhenKeyDoesNotExist() {
    String result = EnvUtils.getEnvVar("KEY", "default_value");

    assertNotNull(result);
    assertEquals("default_value", result);
  }

  @Test
  void testGetEnvVarWithNullDefault() {
    String result = EnvUtils.getEnvVar("KEY", null);

    assertNull(result);
  }

}

