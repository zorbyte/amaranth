package dev.zorbyte.amaranth.tickets.commands;

import dev.zorbyte.amaranth.command.SlashCommand;
import dev.zorbyte.amaranth.tickets.Ticket;
import dev.zorbyte.amaranth.tickets.Ticket.TicketId;
import dev.zorbyte.amaranth.tickets.Tickets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TicketCommand implements SlashCommand {
  private final Tickets tickets;

  @Override
  public SlashCommandData data() {
    return Commands.slash("ticket", "Creates, configures, closes, and adds & removes users from tickets.")
        /* Get Command. */
        .addSubcommands(
            new SubcommandData("debug", "Gets a ticket for debugging purposes.")
                .addOption(OptionType.CHANNEL, "channel", "The ticket channel.", true)
                .addOption(OptionType.INTEGER, "guild-id", "The guild in which the ticket belongs.", false)
        )
        /* Create Command. */
        .addSubcommands(
            new SubcommandData("create", "Presents a menu to help you quickly configure the ticket.")
                // TODO: We will use modals to get the subject of the ticket.
                .addOption(OptionType.STRING, "subject", "The subject of the ticket.", true)
                .addOptions(
                    new OptionData(OptionType.USER, "user", "A user to auto-add to the ticket.", false),
                    new OptionData(OptionType.STRING, "template", "The template to use for the ticket.", false)
                        .addChoice("Default", "default")
                )
        )
        /* Close command. */
        .addSubcommands(
            new SubcommandData("close", "Close a ticket.")
                // If the user doesn't specify a ticket channel to close, we'll attempt to close
                // the ticket in the current channel if present. If no such ticket exists, a
                // modal will be summoned so that the ticket to be closed can be selected by the
                // user instead of erroring out (good UI/UX moment).
                .addOption(OptionType.CHANNEL, "channel", "The ticket channel to close.")
        )
        /* User commands. */
        .addSubcommandGroups(
            new SubcommandGroupData("user", "Add &/ remove user(s) in tickets.").addSubcommands(
                /* Add user command. */
                new SubcommandData("add", "Adds a user to a ticket")
                    .addOption(OptionType.USER, "user", "The user to add to the ticket.", true),
                /* Remove user command. */
                new SubcommandData("remove", "Removes a user from a ticket.")
                    .addOption(
                        OptionType.USER,
                        "user",
                        "The user to remove from the ticket.",
                        true
                    )
            )
        )
        .setContexts(InteractionContextType.GUILD)
        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL));
  }

  // @Override
  // @Transactional
  // public void handle(SlashCommandInteractionEvent event) {
  // switch (event.getSubcommandGroup()) {
  // case "debug":
  // break;
  // case "create":
  // break;
  // case "close":
  // break;
  // case /* user */ "add":
  // break;
  // case /* user */ "remove":
  // break;
  // default:
  // break;
  // }
  // }

  public void sendTicketDebugInfo(SlashCommandData event) {

  }

  private void old(SlashCommandInteractionEvent event) {
    // FIXME: Fix this massive abomination to not be awful.
    // Most of this is jsut test code anyway.
    log.info("Executing ticket command.");
    event.deferReply().queue();
    if (event.getSubcommandName().equals("create")) {
      User user = event.getUser();
      String subject = event.getOption("subject", () -> "Ticket by " + user.getName(), OptionMapping::getAsString);
      Ticket t = new Ticket(subject);
      t.setId(new TicketId(event.getGuild().getIdLong(), event.getChannelIdLong()));
      // t.setChannelId(event.getChannelIdLong());
      t.setCreatorId(event.getUser().getIdLong());
      t = tickets.save(t);
      event.getHook().sendMessage("Ticket created with ID: " + t.getId()).queue();

      return;
    }

    if (event.getSubcommandName().equals("get")) {
      long id = event.getOption("id", OptionMapping::getAsLong);
      if (id == 0L) {
        event.getHook().sendMessage("No such ticket found with ID " + id).queue();
        return;
      }

      Optional<Ticket> potentialTicket = tickets.findById(new TicketId(event.getGuild().getIdLong(), id));
      if (potentialTicket.isEmpty()) {
        event.getHook().sendMessage("No such ticket found with ID " + id).queue();
        return;
      }

      Ticket ticket = potentialTicket.get();
      event.getHook()
          .sendMessage(
              "Found ticket:\nCreator: <@" +
                  ticket.getCreatorId()
                  + ">\nChannel: <#"
                  + ticket.getId().channelId()
                  + ">\nSubject: `"
                  + ticket.getSubject()
                  + "`"
          )
          .queue();
      return;
    }

    event.getHook().sendMessage("Not implemented.");
  }

  // private static class TicketOptions { static getTicketName() {} }
}
