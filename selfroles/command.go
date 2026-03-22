package selfroles

import (
	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/disgo/handler"
	"zorbyte.dev/amaranth/amaranth"
	"zorbyte.dev/amaranth/selfroles/models"
)

func ConfigCommand(a *amaranth.Amaranth, db *models.SelfRolesDB) handler.SlashCommandHandler {
	return func(data discord.SlashCommandInteractionData, e *handler.CommandEvent) error {
		//db.Get(e.Ctx, *data.GuildID(), )

		return nil
	}
}
