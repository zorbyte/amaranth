package dev.zorbyte.amaranth.modules.tickets;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import dev.zorbyte.amaranth.modules.tickets.domain.AddUserToTicketEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TicketUserUseCases {
  @EventListener
  private void userAddedToTicket(AddUserToTicketEvent event) throws UnsupportedOperationException {
    // We are going to try and dispatch business logic to listeners.
    // This is going to be especially handy for TransactionalEventListeners
    // to be employed in future, plus we can leverege Spring's Async feature
    // on that listener to prevent blocking the WS Read Thread when blocking
    // for a pending transaction.
    //
    // TThe last part is an assumption, I will be testing JDAs thread pooling
    // system a bit in the future to see how it interacts with Transactional,
    // it is most likely not going to play well with Spring natives though,
    //
    // I don't have the skills to write a custom thread pool implementation for JDA
    // in Java at this time (I'm a very fast learner, this WILL change), so we can
    // leverge JDA's where that is relevant and Spring's where that is relevant.
    throw new UnsupportedOperationException("Not implemented.");
  }
}
