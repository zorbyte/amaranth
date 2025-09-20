package io.github.zorbyte.discordsentry.commands;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import reactor.core.publisher.Mono;

@Component
public class CommandRegistrar implements ApplicationRunner {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private GatewayDiscordClient gwClient;

  @Autowired
  private RestClient restClient;

  @Autowired
  private List<SlashCommand> commands;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("Uploading commands...");

    final ApplicationService applicationService = this.restClient.getApplicationService();
    final long applicationId = this.restClient.getApplicationId().block();

    final List<ApplicationCommandRequest> requests = this.commands.stream()
        .map(slashCmd -> slashCmd.request())
        .toList();

    /*
     * Bulk overwrite commands. This is now idempotent, so it is safe to use this
     * even when only 1 command is changed/added/removed.
     * 
     * Block the main thread after this in order to keep the application alive.
     */
    applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, requests)
        .doOnNext(cmd -> log.info("Successfully registered Global Command: {}", cmd.name()))
        .doOnError(e -> log.error("Failed to register global commands", e))
        .then(Mono.fromRunnable(() -> log.debug("Registering onDisconnect in order to block application.")))
        .then(this.gwClient.onDisconnect())
        .block();
  }
}
