package dev.zorbyte.amaranth.command;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract interface BaseSlashCommand {
  Logger LOG = LoggerFactory.getLogger(SlashCommand.class);

  /**
   * Gets the name of this slash command.
   *
   * @return The command name
   */
  @Nonnull
  SlashCommandName name();

  /**
   * Checks if this command has subcommands.
   *
   * @return true if the command has subcommands, false otherwise
   */
  boolean hasSubcommands();

  /**
   * Handles the slash command interaction event.
   *
   * @param event The interaction event to handle
   */
  void handle(SlashCommandInteractionEvent event);
}
