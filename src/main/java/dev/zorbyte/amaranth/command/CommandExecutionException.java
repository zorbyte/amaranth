package dev.zorbyte.amaranth.command;

import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;

public class CommandExecutionException extends IllegalStateException {
  public CommandExecutionException() { super("Please try again later."); }

  public CommandExecutionException(Throwable throwable) { super("Please try again later.", throwable); }

  public CommandExecutionException(String msg) { super(msg); }

  public CommandExecutionException(String msg, Throwable throwable) { super(msg, throwable); }

  public Container getUserDisplayableMessage() {
    return Container.of(
        TextDisplay.of("# :warning: Something went wrong..."),
        Separator.createDivider(Separator.Spacing.SMALL),
        TextDisplay.of(this.getMessage())
    );
  }
}
