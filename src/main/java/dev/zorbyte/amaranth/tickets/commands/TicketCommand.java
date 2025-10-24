package dev.zorbyte.amaranth.tickets.commands;

import dev.zorbyte.amaranth.command.SlashCommand;
import dev.zorbyte.amaranth.tickets.Ticket;
import dev.zorbyte.amaranth.tickets.Tickets;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TicketCommand implements SlashCommand {
  private final Tickets tickets;

  @Override
  public SlashCommandData data() {
    return Commands.slash("ticket", "Creates, configures, closes, and adds & removes users from tickets.")

        /* Create Command. */

        .addSubcommands(
            new SubcommandData("get", "Gets a ticket.").addOption(OptionType.INTEGER, "id", "The ID of the ticket."),
            new SubcommandData("create", "Presents a menu to help you quickly configure the ticket.")
                // TODO: We will use modals to get the subject of the ticket.
                .addOption(OptionType.STRING, "subject", "The subject of the ticket.", true)
                .addOptions(
                    new OptionData(OptionType.USER, "user", "A user to auto-add to the ticket."),
                    new OptionData(OptionType.STRING, "template", "The template to use for the ticket.")
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

  @Override
  @Transactional
  public void handle(SlashCommandInteractionEvent event) {
    // FIXME: Fix this massive abomination to not be awful.
    // Most of this is jsut test code anyway.
    LOG.info("Executing ticket command.");
    event.deferReply().queue();
    if (event.getSubcommandName().equals("create")) {
      User user = event.getUser();
      String subject = event.getOption("subject", () -> "Ticket by " + user.getName(), OptionMapping::getAsString);
      Ticket t = new Ticket(subject);
      t.setChannelId(event.getChannelIdLong());
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

      Optional<Ticket> potentialTicket = tickets.findById(id);
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
                  + ticket.getChannelId()
                  + ">\nSubject: `"
                  + ticket.getSubject()
                  + "`"
          )
          .queue();
      return;
    }

    event.getHook().sendMessage("Not implemented.");
  }
}
