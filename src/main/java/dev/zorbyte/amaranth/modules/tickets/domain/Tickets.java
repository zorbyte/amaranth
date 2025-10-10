package dev.zorbyte.amaranth.modules.tickets.domain;

import org.springframework.data.repository.CrudRepository;

public interface Tickets extends CrudRepository<Ticket, Long> {
}
