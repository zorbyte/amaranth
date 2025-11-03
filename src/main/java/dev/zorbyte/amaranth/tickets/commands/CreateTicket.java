package dev.zorbyte.amaranth.tickets.commands;

import dev.zorbyte.amaranth.command.SlashCommand;
import dev.zorbyte.amaranth.command.SlashCommandName;
import dev.zorbyte.amaranth.tickets.internal.TicketChannelUtil;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateTicket implements SlashCommand.Subcommand {
  private final TicketChannelUtil ticketChannelUtil;

  @Override
  public SlashCommandName name() { return new SlashCommandName("ticket", null, "create"); }

  @Override
  public void handle(SlashCommandInteractionEvent event) {
    var guildId = event.getGuild().getIdLong();
    var category = ticketChannelUtil.getOrCreateTicketCategory(event.getGuild().getIdLong());
    var ticketChannel = ticketChannelUtil.createTicketChannel(category, guildId, 0, "ticket");
    // TODO: Put this into a service & do the rest.
  }
}
