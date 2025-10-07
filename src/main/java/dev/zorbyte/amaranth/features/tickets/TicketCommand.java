package dev.zorbyte.amaranth.features.tickets;

import org.springframework.stereotype.Component;

import dev.zorbyte.amaranth.discord.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.*;
import net.dv8tion.jda.api.interactions.commands.build.*;

@Component
public class TicketCommand implements SlashCommand {
  @Override
  public SlashCommandData data() {
    return Commands.slash("ticket", "Manages tickets for the server.")
        .addSubcommandGroups(
            new SubcommandGroupData("user", "Deals with managing users in a ticket.").addSubcommands(
                new SubcommandData("add", "Adds a user to a ticket")
                    .addOption(OptionType.USER, "user", "The user to add to the ticket.", true),
                new SubcommandData("remove", "Removes a user from a ticket.")
                    .addOption(OptionType.USER, "user", "The user to remove from the ticket.",
                        true)))
        .addSubcommands(
            new SubcommandData("create", "Deals with managing users in a ticket.").addOptions(
                new OptionData(OptionType.USER, "user", "A user to auto-add to the ticket."),
                new OptionData(OptionType.STRING, "template", "The template to use for the ticket.")
                    .addChoice("Default", "default")))
        .addSubcommands(new SubcommandData("close", "Close a ticket.")
            // If the user doesn't specify a ticket channel to close, we'll attempt to close
            // the ticket in the current channel if present. If no such ticket exists, a
            // modal will be summoned so that the ticket to be closed can be selected by the
            // user instead of erroring out (good UI/UX moment).
            .addOption(OptionType.CHANNEL, "channel", "The ticket channel to close."))
        .setContexts(InteractionContextType.GUILD)
        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL));
  }

  @Override
  public void handle(SlashCommandInteractionEvent event) {
    String name = event.getInteraction().getName();
    log.info("Command executed: {}", name);
  }
}
