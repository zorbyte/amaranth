package io.github.zorbyte.discordsentry.commands;

import org.springframework.stereotype.Component;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.*;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

@Component
public class GreetCommand implements SlashCommand {
  @Override
  public ApplicationCommandRequest request() {
    return ApplicationCommandRequest.builder()
        .name("greet")
        .description("Greets You")
        .addOption(ApplicationCommandOptionData.builder()
            .name("name")
            .description("Your name")
            .type(ApplicationCommandOption.Type.STRING.getValue())
            .required(true)
            .build())
        .build();
  }

  @Override
  public Mono<Void> handle(ChatInputInteractionEvent event) {
    String name = event.getOption("name")
        .flatMap(ApplicationCommandInteractionOption::getValue)
        .map(ApplicationCommandInteractionOptionValue::asString)
        .get(); // This is warning us that we didn't check if its present, we can ignore this on
                // required options

    // Reply to the slash command, with the name the user supplied
    return event.reply()
        .withEphemeral(true)
        .withContent("Hello, " + name);
  }
}