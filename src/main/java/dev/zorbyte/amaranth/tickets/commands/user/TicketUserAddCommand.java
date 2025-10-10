package dev.zorbyte.amaranth.tickets.commands.user;

import org.springframework.stereotype.Component;

import dev.zorbyte.amaranth.command.SlashCommand;
import dev.zorbyte.amaranth.command.SlashCommandName;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Component
public class TicketUserAddCommand implements SlashCommand.Subcommand {
  @Override
  public SlashCommandName name() { return new SlashCommandName("ticket", "user", "add"); }

  @Override
  public void handle(SlashCommandInteractionEvent event) {
    event.reply(
        "Hello " + event.getOption("user", OptionMapping::getAsUser).getAsMention() + " from " + name()
    ).queue();
  }
}
