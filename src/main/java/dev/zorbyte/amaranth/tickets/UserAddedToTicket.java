package dev.zorbyte.amaranth.tickets;

/**
 * Event that is dispatched when a user is added to a ticket.
 */
public record UserAddedToTicket(long ticketId, long userId) {}
