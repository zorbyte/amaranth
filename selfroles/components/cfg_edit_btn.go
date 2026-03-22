package components

import (
	"github.com/disgoorg/disgo/discord"
	"zorbyte.dev/amaranth/amaranth/ui"
)

func EditButton(customID string) discord.ButtonComponent {
	return discord.NewPrimaryButton(ui.ZeroWidthSpace+"   Edit", customID).
		WithEmoji(ui.EditEmoji.ComponentEmoji())
}
