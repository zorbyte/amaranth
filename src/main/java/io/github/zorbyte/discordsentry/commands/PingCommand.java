package io.github.zorbyte.discordsentry.commands;

import org.springframework.stereotype.Component;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@Component
public class PingCommand implements SlashCommand {
  @Override
  public ApplicationCommandRequest request() {
    return ApplicationCommandRequest.builder()
        .name("ping")
        .description("Ping pong!")
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    // We reply to the command with "Pong!" and make sure it is ephemeral (only the
    // command user can see it)
    return event.reply()
        .withEphemeral(true)
        .withContent("Pong!");
  }
}
