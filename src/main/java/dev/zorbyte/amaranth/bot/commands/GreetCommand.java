package dev.zorbyte.amaranth.bot.commands;

import org.springframework.stereotype.Component;

import dev.zorbyte.amaranth.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Component
public class GreetCommand implements SlashCommand {
  @Override
  public SlashCommandData data() {
    return Commands.slash("greet", "Greets you").addOption(OptionType.STRING, "name", "Your name", true);
  }

  @Override
  public void handle(SlashCommandInteractionEvent event) {
    String name = event.getOption("name").getAsString();

    // Reply to the slash command, with the name the user supplied
    event.reply("Hello, " + name).setEphemeral(true).queue();
  }
}
