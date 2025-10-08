package dev.zorbyte.amaranth.discord;

import java.util.Collection;
import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Slf4j
@Configuration
public class DiscordConfig {
  private static EnumSet<GatewayIntent> ENABLED_GATEWAY_INTENTS = EnumSet.of(
      // Enables MessageReceivedEvent for guild (also known as servers)
      GatewayIntent.GUILD_MESSAGES,
      // Enables access to message.getContentRaw()
      GatewayIntent.MESSAGE_CONTENT);

  // Removes warnings from the console that we don't have the intents enabled for
  // these caches to be relevant.
  private static EnumSet<CacheFlag> DISABLED_CACHE_FLAGS = EnumSet.of(
      CacheFlag.VOICE_STATE, CacheFlag.EMOJI,
      CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);

  @Value("${discord.token}")
  private String token;

  @Value("${discord.activity}")
  private String status;

  @Autowired
  private ApplicationContext applicationContext;

  private JDA jda;

  @Bean
  JDA createDiscordAPI() {
    try {
      this.jda = JDABuilder.createDefault(this.token)
          .setActivity(Activity.customStatus(this.status))
          .setEnabledIntents(ENABLED_GATEWAY_INTENTS)
          .disableCache(DISABLED_CACHE_FLAGS)
          .build();

      return this.jda;
    } catch (Exception e) {
      log.error("An exception occurred while setting up the JDA bean: ", e);
      return null;
    }
  }

  @EventListener
  // TODO: Investigate context started and friends to see best way to slot this
  // into the application lifecycle.
  public void onApplicationEvent(ContextRefreshedEvent event) {
    // We add the listener adapters to the JDA here so that we don't have circular
    // calls from beans. Most logic in the application stems from JDA events calling
    // it, so registering any listeners in the JDA's Bean declaration would make it
    // very difficult to build our project, especially if any beans that need to
    // inject JDA are in someway called from an event.
    Collection<ListenerAdapter> listenerAdapters = applicationContext.getBeansOfType(ListenerAdapter.class).values();
    this.jda.addEventListener(listenerAdapters.toArray());
  }
}
