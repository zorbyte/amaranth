package dev.zorbyte.amaranth.core.command;

import lombok.NonNull;

import java.util.Objects;

import jakarta.annotation.Nullable;

public record SlashCommandName(@NonNull String root, @Nullable String group, @Nullable String subcommand) {
  public SlashCommandName(String root) { this(root, null, null); }

  @Override
  public final boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof SlashCommandName)) return false;
    SlashCommandName other = (SlashCommandName) o;

    return root().equals(other.root())
        && group().equals(other.group())
        && subcommand().equals(other.subcommand());
  }

  @Override
  public final String toString() {
    String commandName = root();
    if (group() != null) commandName += " " + group();
    if (subcommand() != null) commandName += " " + subcommand();
    return commandName;
  }

  @Override
  public final int hashCode() { return Objects.hash(root(), group(), subcommand()); }
}
