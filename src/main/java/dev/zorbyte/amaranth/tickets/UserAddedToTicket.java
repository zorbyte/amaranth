package dev.zorbyte.amaranth.tickets;

// Event that we will dispatch so that our business logic can listen in.
public record UserAddedToTicket(Ticket userId) {}
