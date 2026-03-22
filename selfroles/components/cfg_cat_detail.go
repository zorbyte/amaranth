package components

import (
	"github.com/disgoorg/disgo/discord"
	"zorbyte.dev/amaranth/selfroles/models"
	"zorbyte.dev/amaranth/selfroles/templates"
)

func CategoryDetails(category *models.Category) discord.SectionComponent {
	detailsText := templates.RenderTemplate("category_details", category)
	return discord.NewSection(discord.NewTextDisplay(detailsText)).
		WithAccessory(EditButton("/selfroles/1/details"))
}
