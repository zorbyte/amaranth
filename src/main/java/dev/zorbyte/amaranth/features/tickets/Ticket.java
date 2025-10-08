package dev.zorbyte.amaranth.features.tickets;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@RequiredArgsConstructor
public class Ticket {
  @Id
  @Getter
  private long id;
}
