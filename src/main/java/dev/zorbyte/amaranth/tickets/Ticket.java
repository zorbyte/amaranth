package dev.zorbyte.amaranth.tickets;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class Ticket {
  @Id()
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Nonnull
  private String subject;

  private long creatorId;
  private long channelId;
}
