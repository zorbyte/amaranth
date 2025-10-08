package dev.zorbyte.amaranth.discord;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Slf4j
@Component
public class InteractionDispatcher extends ListenerAdapter {
  @Autowired
  private CommandRegistry commandRegistry;

  @Override
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    final Optional<SlashCommand> potentialCmd = this.commandRegistry.getSlashCommand(event.getName());
    if (potentialCmd.isEmpty())
      return;

    final SlashCommand cmd = potentialCmd.get();
    try {
      cmd.handle(event);
    } catch (Exception e) {
      log.error("An error occurred while dispatching a slash command:\n" + "\tinteractionCommandName={}"
          + "\tcommandClassFound: {}", event.getName(), e);
    }
  }
}
