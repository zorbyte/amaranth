package dev.zorbyte.amaranth.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface SlashCommand extends BaseSlashCommand {
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

  // Acessible via SlashCommand.Subcommand
  public interface Subcommand extends BaseSlashCommand {
    @Override
    default boolean hasSubcommands() { return false; }
  }
}
