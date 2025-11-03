package dev.zorbyte.amaranth.tickets;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Ticket {
  @EmbeddedId
  private TicketId id;

  private int localId;

  private long creatorId;

  @Nonnull
  private String subject;

  @OneToMany(
      mappedBy = "ticket",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<TicketUser> users = new ArrayList<>();

  public record TicketId(long guildId, long channelId) {}
}
