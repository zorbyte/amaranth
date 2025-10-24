package dev.zorbyte.amaranth.bot.internal;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.EnumSet;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
class JDAConfig {
  private static final EnumSet<GatewayIntent> ENABLED_GATEWAY_INTENTS = EnumSet.of(
      // Enables MessageReceivedEvent for guild (also known as servers)
      GatewayIntent.GUILD_MESSAGES,
      // Enables access to message.getContentRaw()
      GatewayIntent.MESSAGE_CONTENT
  );

  // Removes warnings from the console that we don't have the intents enabled for
  // these caches to be relevant.
  private static final EnumSet<CacheFlag> DISABLED_CACHE_FLAGS = EnumSet.of(
      CacheFlag.VOICE_STATE,
      CacheFlag.EMOJI,
      CacheFlag.STICKER,
      CacheFlag.SCHEDULED_EVENTS
  );

  @Value("${discord.token}")
  private String token;

  @Value("${discord.activity}")
  private String status;

  private JDA jda;

  @Bean
  JDA createDiscordAPI(ApplicationEventPublisher publisher) {
    try {
      jda = JDABuilder.createDefault(token)
          .setActivity(Activity.customStatus(status))
          .setEnabledIntents(ENABLED_GATEWAY_INTENTS)
          .disableCache(DISABLED_CACHE_FLAGS)
          .setEnableShutdownHook(true)
          .addEventListeners((net.dv8tion.jda.api.hooks.EventListener) (GenericEvent event) -> {
            try {
              // This allows us to use the @EventListener spring annotation to listen for JDA
              // events.
              publisher.publishEvent(new PayloadApplicationEvent<>(event.getJDA(), event));
            } catch (Exception e) {
              log.error("An error occurred while dispatching a JDA event. ({})", event.getClass().getSimpleName(), e);
            }
          })
          .build();
      return jda;
    } catch (Exception e) {
      log.error("An exception occurred while setting up the JDA bean: ", e);
      return null;
    }
  }

  @EventListener
  private void onReady(ReadyEvent event) {
    SelfUser selfUser = event.getJDA().getSelfUser();
    log.info("Logged in as {} ({})", selfUser.getAsTag(), selfUser.getId());
  }

  @EventListener
  private void onException(ExceptionEvent event) {
    log.error("An exception event has been thorwn by JDA:", event.getCause());
  }

  @PreDestroy
  private void onShutdown() throws InterruptedException {
    try {
      // Initial shutdown, allowing for some RestActions to still go through
      log.info("Shutting down JDA instance...");
      jda.shutdown();
      // Wait up to 10 seconds for requests to finish
      if (!jda.awaitShutdown(Duration.ofSeconds(10))) {
        jda.shutdownNow(); // Cancel request queue.
        jda.awaitShutdown();
      }

      log.info("JDA shutdown complete.");
    } catch (InterruptedException e) {
      log.error("An interrupt exception occurred while trying to gracefuly shutdown JDA.", e);
      throw e;
    }
  }
}
