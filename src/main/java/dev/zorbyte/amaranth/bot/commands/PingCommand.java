package dev.zorbyte.amaranth.bot.commands;

import dev.zorbyte.amaranth.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Component;

@Component
public class PingCommand implements SlashCommand {
  @Override
  public SlashCommandData data() { return Commands.slash("ping", "Pongs back."); }

  @Override
  public void handle(SlashCommandInteractionEvent event) {
    // We reply to the command with "Pong!" and make sure it is ephemeral (only the
    // command user can see it)
    event.reply("Pong!").queue();
  }
}
