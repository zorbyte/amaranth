package dev.zorbyte.amaranth;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.system.SystemProperties;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    loadDotenv();

    new SpringApplicationBuilder(Application.class)
        .bannerMode(Banner.Mode.OFF)
        .build()
        .run(args);
  }

  private static void loadDotenv() {
    try {
      log.debug("Loading .env configuration(s)...");

      Dotenv
          .configure()
          .systemProperties()
          .load();
    } catch (DotenvException e) {
      // Shouldn't crash the app, Spring has many other ways for us to attempt the
      // correct configs.
      log.error("An exception occurred while loading .env configuration(s): ", e);
    }
  }
}
