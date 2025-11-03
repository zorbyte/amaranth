package dev.zorbyte.amaranth.tickets.commands;

import dev.zorbyte.amaranth.command.SlashCommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TicketCommand implements SlashCommand {
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
}
