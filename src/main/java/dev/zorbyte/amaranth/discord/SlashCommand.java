package dev.zorbyte.amaranth.discord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface SlashCommand {
  final Logger log = LoggerFactory.getLogger(SlashCommand.class);

  SlashCommandData data();

  void handle(SlashCommandInteractionEvent event);
}
