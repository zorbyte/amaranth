package dev.zorbyte.amaranth.command.internal;

import dev.zorbyte.amaranth.command.BaseSlashCommand;
import dev.zorbyte.amaranth.command.SlashCommand;
import dev.zorbyte.amaranth.command.SlashCommandName;
import io.micrometer.common.lang.Nullable;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
class CommandRegistrar {
  private JDA jda;

  private List<SlashCommand> slashCommands;
  private final List<SlashCommand.Subcommand> subcommands;

  /*
   * Bulk overwrites commands for a guild.
   *
   * This is now idempotent, so it is safe to use this even when only 1 command is
   * changed/added/removed.
   */
  void uploadGuildSlashCommands(long guildID) {
    final List<SlashCommandData> requests = slashCommands.stream().map(SlashCommand::data).toList();

    jda.getGuildById(guildID)
        .updateCommands()
        .addCommands(requests)
        .queue(
            _ -> log.info("Uploaded commands to discord guild ({}).", guildID),
            e -> log.error("An error occurred while uploading commands to a discord guild ({}):", guildID, e)
        );
  }

  /*
   * Bulk overwrites commands.
   *
   * This is now idempotent, so it is safe to use this even when only 1 command is
   * changed/added/removed.
   */
  void uploadGlobalSlashCommands() {
    log.info("Uploading global application commands to Discord...");
    final List<SlashCommandData> requests = slashCommands.stream().map(SlashCommand::data).toList();

    jda.updateCommands()
        .addCommands(requests)
        .queue(
            _ -> log.info("Succesfuly uploaded global application commands to discord."),
            e -> log.error("An error occurred while uploading global application commands to Discord.", e)
        );
  }

  public Optional<BaseSlashCommand> getSlashCommand(
      @NonNull String name,
      @Nullable String group,
      @Nullable String subcommand
  ) {
    // Filter out all slash commands that don't match the name this event is for.
    return slashCommands.stream()
        .filter(slashCmd -> slashCmd.name().root().equals(name))
        .findFirst()
        .flatMap(slashCmd -> {
          if (!slashCmd.hasSubcommands()) return Optional.of(slashCmd);

          return subcommands.stream()
              .filter(subCmd -> subCmd.name().equals(new SlashCommandName(name, group, subcommand)))
              .findFirst();
        });
  }
}
