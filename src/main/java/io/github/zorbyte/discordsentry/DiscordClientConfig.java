package io.github.zorbyte.discordsentry;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.rest.RestClient;

import io.github.zorbyte.discordsentry.events.EventListener;

@Configuration
public class DiscordClientConfig {
  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Value("${bot.token}")
  private String token;

  @Bean
  <T extends Event> GatewayDiscordClient gatewayDiscordClient(List<EventListener<T>> eventListeners) {
    try {
      GatewayDiscordClient client = DiscordClientBuilder.create(this.token)
          .build()
          .gateway()
          .setInitialPresence(_shard -> ClientPresence.online(ClientActivity.listening("to /commands")))
          .login()
          .block();

      for (EventListener<T> listener : eventListeners) {
        client.on(listener.getEventType())
            .flatMap(listener::handle)
            .onErrorResume(listener::handleError)
            .subscribe();

        log.info("Registered event handler: {}", listener.getEventType().getSimpleName());
      }

      return client;
    } catch (Exception e) {
      log.error("An exception occurred while setting up the gateway bean: ", e);
    }

    return null;
  }

  @Bean
  RestClient discordRestClient(GatewayDiscordClient client) {
    return client.getRestClient();
  }
}
