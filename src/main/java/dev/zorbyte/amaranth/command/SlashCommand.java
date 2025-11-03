package dev.zorbyte.amaranth.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * Interface for Discord slash commands. Extends BaseSlashCommand to provide
 * command functionality.
 */
public interface SlashCommand extends BaseSlashCommand {
  /**
   * Gets the command data that defines this slash command.
   *
   * @return The slash command data
   */
  SlashCommandData data();

  @Override
  default SlashCommandName name() { return new SlashCommandName(data().getName()); }

  @Override
  default boolean hasSubcommands() {
    return !data().getSubcommandGroups().isEmpty() || !data().getSubcommands().isEmpty();
  }

  @Override
  default void handle(SlashCommandInteractionEvent event) {
    log.debug("Passing over execution of slash command handler for \"{}\"", event.getFullCommandName());
  }

  /**
   * Interface for subcommands of a slash command. Implements BaseSlashCommand
   * with default behavior for subcommands.
   */
  public interface Subcommand extends BaseSlashCommand {
    @Override
    default boolean hasSubcommands() { return false; }
  }
}
