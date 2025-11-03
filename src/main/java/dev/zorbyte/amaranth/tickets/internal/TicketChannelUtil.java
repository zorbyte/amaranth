package dev.zorbyte.amaranth.tickets.internal;

import dev.zorbyte.amaranth.command.CommandExecutionException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TicketChannelUtil {
  private static final String CATEGORY_NAME = "Tickets";

  private final JDA jda;

  // private final String categoryName;

  // @Autowired
  // public TicketChannelUtil(JDA jda) { this(jda, "Tickets"); }

  // public TicketChannelUtil(JDA jda, String categoryName) {
  // this.categoryName = categoryName;
  // this.jda = jda;
  // }

  /**
   * Creates a channel for a ticket in the specified guild. Will throw if the bot
   * does not have the `MANAGE_CHANNEL` permission.
   *
   * @param category {@link net.dv8tion.jda.api.entities.channel.concrete.Category}
   * @param guildId  long The ID in which the channel will be created.
   * @param localId  long The ID to be put into the channel name.
   * @param name     String The name of the channel.
   * @return {@link net.dv8tion.jda.api.entities.channel.concrete.TextChannel}
   * @throws CommandExecutionException When the bot doesn't have the
   *                                   `MANAGE_CHANNEL` permission.
   */
  public TextChannel createTicketChannel(Category category, long guildId, int localId, String name) {
    assertHasManageChannelsPermission(guildId);
    var guild = mustGetGuild(guildId);

    var ticketName = String.valueOf(localId) + "-" + name;
    if (ticketName.length() > 100) ticketName = ticketName.substring(0, 100);

    return guild.createTextChannel(ticketName, category).complete();
  }

  /**
   * Gets the category called tickets to create all tickets under. Will also
   * verify if the bot has the `MANAGE_CHANNEL` permission.
   *
   * @param guildId The ID of the guild to create the category in.
   * @return {@link net.dv8tion.jda.api.entities.channel.concrete.Category}
   * @throws CommandExecutionException When the bot doesn't have the
   *                                   `MANAGE_CHANNEL` permission.
   */
  public Category getOrCreateTicketCategory(long guildId) {
    var guild = mustGetGuild(guildId);
    var categoryCandidates = guild.getCategoriesByName(CATEGORY_NAME, true);
    if (!categoryCandidates.isEmpty()) return categoryCandidates.get(0);
    assertHasManageChannelsPermission(guildId);
    // TODO: Investigate if we should return the Future or block here.
    return guild.createCategory(CATEGORY_NAME).complete();
  }

  /**
   * Ensures the bot has permissions to `MANAGE_CHANNEL`, will throw if not.
   *
   * @param guildId The ID of the guild
   * @throws CommandExecutionException
   */
  public void assertHasManageChannelsPermission(long guildId) throws CommandExecutionException {
    var guild = mustGetGuild(guildId);
    var hasPerm = guild.getSelfMember().hasPermission(Permission.MANAGE_CHANNEL);
    if (hasPerm) return;

    // TODO: In future, a gif should be attached to this so that the user knows how
    // to do this.
    throw new CommandExecutionException(
        "Please add the `MANAGE_CHANNELS` permission to the bot so we can make a ticket channel!"
    );
  }

  private Guild mustGetGuild(long guildId) throws IllegalStateException {
    var guild = jda.getGuildById(guildId);
    if (guild == null) throw new IllegalStateException("Unable to resolve guild in guild scoped handler.");
    return guild;
  }
}
