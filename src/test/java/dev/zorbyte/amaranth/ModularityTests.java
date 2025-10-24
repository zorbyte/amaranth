package dev.zorbyte.amaranth;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {
  ApplicationModules modules = ApplicationModules.of(Application.class);

  @Test
  void verifyModularity() {
    modules.verify();
  }

  @Test
  void writeDocumentationSnippets() { // NOSONAR java:S2699
    new Documenter(modules)
        .writeModulesAsPlantUml()
        .writeIndividualModulesAsPlantUml();
  }
}
