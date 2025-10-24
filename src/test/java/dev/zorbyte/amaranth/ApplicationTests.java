package dev.zorbyte.amaranth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {
  @Test
  void contextLoads() {
    // Tests if the spring context builds successfully and no circular dependencies
    // exist.
  }
}
