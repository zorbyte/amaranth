package dev.zorbyte.amaranth.command.internal;

import dev.zorbyte.amaranth.command.BaseSlashCommand;
import dev.zorbyte.amaranth.command.CommandExecutionException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
class InteractionDispatcher {
  private final CommandRegistrar commandRegistry;

  @EventListener(SlashCommandInteractionEvent.class)
  private void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
    commandRegistry.getSlashCommand(event.getName(), event.getSubcommandGroup(), event.getSubcommandName())
        .ifPresent(cmd -> executeSlashCommand(event, cmd));
  }

  private void executeSlashCommand(@NonNull SlashCommandInteractionEvent event, @NonNull BaseSlashCommand slashCmd) {
    Container userFacingErrMsgComponents = null;

    try {
      slashCmd.handle(event);
    } catch (CommandExecutionException e) {
      userFacingErrMsgComponents = e.getUserDisplayableMessage();
    } catch (Exception e) {
      log.error(
          "An error occurred while dispatching a slash command:\n"
              + "\tinteractionCommandName={}"
              + "\tcommandClassFound: {}",
          event.getName(),
          e
      );

      userFacingErrMsgComponents = new CommandExecutionException(
          "A critical error occurred while executing the given command. Please try again leater.",
          e
      ).getUserDisplayableMessage();
    }

    if (userFacingErrMsgComponents == null) return;

    if (event.isAcknowledged()) {
      event
          .getHook()
          .sendMessageComponents(userFacingErrMsgComponents)
          .useComponentsV2()
          .queue();
    } else {
      event
          .replyComponents(userFacingErrMsgComponents)
          .useComponentsV2()
          .setEphemeral(true)
          .queue();
    }
  }
}
