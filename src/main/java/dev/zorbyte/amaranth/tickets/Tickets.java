package dev.zorbyte.amaranth.tickets;

import org.springframework.data.repository.CrudRepository;

public interface Tickets extends CrudRepository<Ticket, Long> {}
