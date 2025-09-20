package io.github.zorbyte.discordsentry.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import discord4j.core.event.domain.Event;
import reactor.core.publisher.Mono;

public interface EventListener<T extends Event> {
  final Logger log = LoggerFactory.getLogger(EventListener.class);

  public Class<T> getEventType();

  public Mono<Void> handle(T event);

  public default Mono<Void> handleError(Throwable error) {
    log.error("Unable to process " + getEventType().getSimpleName(), error);
    return Mono.empty();
  }
}