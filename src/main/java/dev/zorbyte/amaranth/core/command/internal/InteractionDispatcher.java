package dev.zorbyte.amaranth.core.command.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.zorbyte.amaranth.core.command.BaseSlashCommand;
import dev.zorbyte.amaranth.core.command.CommandRegistrar;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Slf4j
@Component
class InteractionDispatcher {
  @Autowired
  private CommandRegistrar commandRegistry;

  @EventListener(SlashCommandInteractionEvent.class)
  private void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    commandRegistry.getSlashCommand(event.getName(), event.getSubcommandGroup(), event.getSubcommandName())
        .ifPresent(cmd -> executeSlashCommand(event, cmd));
  }

  private void executeSlashCommand(@NonNull SlashCommandInteractionEvent event, @NonNull BaseSlashCommand slashCmd) {
    try {
      slashCmd.handle(event);
    } catch (Exception e) {
      log.error(
          "An error occurred while dispatching a slash command:\n"
              + "\tinteractionCommandName={}"
              + "\tcommandClassFound: {}",
          event.getName(),
          e
      );
    }
  }
}
