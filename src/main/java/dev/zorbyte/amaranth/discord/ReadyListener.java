package dev.zorbyte.amaranth.discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
@Component
public class ReadyListener extends ListenerAdapter {
  @Value("${discord.testGuildID}")
  private long testGuildID = 0L;

  @Autowired
  Environment env;

  @Autowired
  private CommandRegistry commandRegistry;

  @Override
  public void onReady(ReadyEvent event) {
    SelfUser selfUser = event.getJDA().getSelfUser();
    log.info("Logged in as {} ({})", selfUser.getAsTag(), selfUser.getId());

    if (this.env.matchesProfiles("dev") && this.testGuildID != 0L) {
      log.info("Application was launched in development mode... Upserting commands to test guild ({}).",
          this.testGuildID);
      this.commandRegistry.addAllSlashCommandsToGuild(testGuildID);
    }
  }

  @Override
  public void onException(ExceptionEvent event) {
    log.error("An exception event has been thorwn:", event.getCause());
  }
}
