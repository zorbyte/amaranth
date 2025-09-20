package io.github.zorbyte.discordsentry.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

public interface SlashCommand {
  ApplicationCommandRequest request();

  Mono<Void> handle(ChatInputInteractionEvent event);
}
