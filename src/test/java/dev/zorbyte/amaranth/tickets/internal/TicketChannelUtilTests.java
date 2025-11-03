package dev.zorbyte.amaranth.tickets.internal;

import static org.junit.Assert.assertThrows;

import org.aspectj.lang.annotation.Before;

import dev.zorbyte.amaranth.command.CommandExecutionException;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class TicketChannelUtilTests {
  @Autowired
  private JDA jda;

  @Autowired
  private TicketChannelUtil util;

  @Value("${discord.testFailuresGuildID}")
  private long testFailuresGuildId;

  @Value("${discord.testGuildID}")
  private long testGuildId;

  private Category ticketsCategory;

  private TextChannel ticketChannel;

  @BeforeAll
  void ensureAllGuildsLoaded() throws InterruptedException {
    log.info("Awaiting ready event before testing Discord API calls so that all guilds are cached");
    jda.awaitReady();
  }

  @Test
  @DisplayName("Throws a CommandExecutionError when the `MANAGE_CHANNEL` permission is **not** present.")
  void failsWhenNoManageChannelPermissionPresent() {
    assertThrows(
        CommandExecutionException.class,
        () -> {
          util.assertHasManageChannelsPermission(testFailuresGuildId);
        }
    );
  }

  @Test
  @DisplayName("It does not throw an error when the bot has the `MANAGE_CHANNEL` permission that it requires.")
  void itCanCorrectlyDetectWhenTheManageChannelsPermissionIsPresent() {
    util.assertHasManageChannelsPermission(testGuildId);
  }

  @Test
  @DisplayName("It creates a ticket category succesfuly.")
  void itCreatesATicketCategory() { ticketsCategory = util.getOrCreateTicketCategory(testGuildId); } // NOSONAR

  @Test
  @DisplayName("It creates a ticket channel under the tickets category.")
  void itCreatesATicketChannel() { // NOSONAR
    ticketChannel = util.createTicketChannel(ticketsCategory, testGuildId, 1, "test-ticket");
  }

  @AfterAll
  void removeTicketsCategory() { this.ticketsCategory.delete().complete(); }

  @AfterAll
  void removeTicketChannel() { this.ticketChannel.delete().complete(); }
}
