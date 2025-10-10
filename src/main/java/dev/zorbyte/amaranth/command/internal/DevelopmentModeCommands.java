package dev.zorbyte.amaranth.command.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.session.ReadyEvent;

@Slf4j
@Component
@Profile("dev")
class DevelopmentModeCommands {
  @Value("${discord.testGuildID:0}")
  private long testGuildID;

  @Autowired
  private CommandRegistrar commandRegistry;

  @EventListener
  private void addCommandsOnReady(ReadyEvent event) {
    if (this.testGuildID != 0L) {
      log.info(
          "Application was launched in development mode... Uploading application commands to test guild ({}).",
          testGuildID
      );

      commandRegistry.uploadGuildSlashCommands(testGuildID);
    }
  }
}
