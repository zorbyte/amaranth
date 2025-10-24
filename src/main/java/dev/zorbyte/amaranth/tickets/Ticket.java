package dev.zorbyte.amaranth.tickets;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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

  @OneToMany(
      mappedBy = "ticket",
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<TicketUser> users = new ArrayList<>();

  private long creatorId;
  private long channelId;
}
