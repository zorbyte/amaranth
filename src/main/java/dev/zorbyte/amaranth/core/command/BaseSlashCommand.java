package dev.zorbyte.amaranth.core.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract interface BaseSlashCommand {
  final Logger log = LoggerFactory.getLogger(SlashCommand.class);

  @Nonnull
  SlashCommandName name();

  boolean hasSubcommands();

  void handle(SlashCommandInteractionEvent event);
}
