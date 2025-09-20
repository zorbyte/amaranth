package io.github.zorbyte.discordsentry.events;

import org.springframework.stereotype.Component;

import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

@Component
public class ReadyListener implements EventListener<ReadyEvent> {
  public Class<ReadyEvent> getEventType() {
    return ReadyEvent.class;
  }

  public Mono<Void> handle(ReadyEvent event) {
    return Mono.fromRunnable(() -> {
      final User self = event.getSelf();
      log.info("Logged in as {}", self.getTag());
    });
  }
}
