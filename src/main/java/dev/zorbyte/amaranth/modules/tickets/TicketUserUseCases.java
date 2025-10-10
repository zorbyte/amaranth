package dev.zorbyte.amaranth.modules.tickets;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.zorbyte.amaranth.modules.tickets.domain.AddUserToTicketEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketUserUseCases {

  @EventListener
  private void userAddedToTicket(AddUserToTicketEvent event) {
  }
}
