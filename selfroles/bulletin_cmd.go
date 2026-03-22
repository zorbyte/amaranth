package selfroles

import (
	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/disgo/handler"
	"zorbyte.dev/amaranth/amaranth"
)

func bulletinMessageCommand(a *amaranth.Amaranth) handler.CommandHandler {
	command.Options = append(command.Options, discord.ApplicationCommandOptionSubCommand{
		Name:        "bulletin",
		Description: "Creates the bulletin message with the button to open the self roles picker.",
	})

	return func(e *handler.CommandEvent) error {
		if err := e.DeferCreateMessage(true); err != nil {
			return err
		}

		if _, err := a.Client.Rest.CreateMessage(
			e.Channel().ID(),
			discord.NewMessageCreateV2(discord.NewContainer(discord.NewSection(
				discord.NewTextDisplay("### Add/remove roles here:")).
				WithAccessory(discord.
					NewSecondaryButton("Role Selector", "/menu").
					WithEmoji(discord.NewComponentEmoji("🎭")),
				),
			))); err != nil {
			return err
		}

		_, err := e.CreateFollowupMessage(discord.NewMessageCreate().WithContent(
			"Successfully created bulletin message",
		))

		return err
	}
}
