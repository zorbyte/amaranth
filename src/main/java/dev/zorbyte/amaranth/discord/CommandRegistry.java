package dev.zorbyte.amaranth.discord;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Slf4j
@Service
public class CommandRegistry {
  @Autowired
  private JDA jda;

  @Autowired
  private List<SlashCommand> commands;

  public void addAllSlashCommandsToGuild(long guildID) {
    final List<SlashCommandData> requests = this.commands.stream()
        .map(slashCmd -> slashCmd.data())
        .toList();

    /*
     * Bulk overwrite commands. This is now idempotent, so it is safe to use this
     * even when only 1 command is changed/added/removed.
     * 
     * Block the main thread after this in order to keep the application alive.
     */
    final Guild guild = this.jda.getGuildById(guildID);
    guild.updateCommands().addCommands(requests).queue(
        _cmds -> log.info("Uploaded commands to discord guild ({}).", guildID),
        e -> log.error("An error occurred while uploading commands to a discord guild ({}):\n {}", guildID, e));
  }

  public Optional<SlashCommand> getSlashCommand(String name) {
    return this.commands.stream() // Filter out all commands that don't match the name this event is for
        .filter(command -> command.data().getName().equals(name))
        .findFirst();
  }
}
