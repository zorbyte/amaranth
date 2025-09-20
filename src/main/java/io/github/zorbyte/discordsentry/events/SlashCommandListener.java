package io.github.zorbyte.discordsentry.events;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import io.github.zorbyte.discordsentry.commands.SlashCommand;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SlashCommandListener implements EventListener<ChatInputInteractionEvent> {
  @Autowired
  private List<SlashCommand> commands;

  @Override
  public Class<ChatInputInteractionEvent> getEventType() {
    return ChatInputInteractionEvent.class;
  }

  public Mono<Void> handle(ChatInputInteractionEvent event) {
    // Convert our list to a flux that we can iterate through
    return Flux.fromIterable(commands)
        // Filter out all commands that don't match the name this event is for
        .filter(command -> command.request().name().equals(event.getCommandName()))
        // Get the first (and only) item in the flux that matches our filter
        .next()
        // Have our command class handle all logic related to its specific command.
        .flatMap(command -> command.handle(event));
  }
}