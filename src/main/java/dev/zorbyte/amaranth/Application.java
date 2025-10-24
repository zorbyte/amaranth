package dev.zorbyte.amaranth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Amaranth Discord bot. Initializes Spring Boot
 * application context and starts the bot.
 */
@SpringBootApplication
public class Application {
  /**
   * Main entry point for the application.
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) { SpringApplication.run(Application.class, args); }
}
