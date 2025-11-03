package dev.zorbyte.amaranth.tickets;

import dev.zorbyte.amaranth.tickets.Ticket.TicketId;
import org.springframework.data.repository.CrudRepository;

public interface Tickets extends CrudRepository<Ticket, TicketId> {}
