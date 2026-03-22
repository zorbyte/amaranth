package components

import (
	"github.com/disgoorg/disgo/discord"
	"zorbyte.dev/amaranth/selfroles/models"
	"zorbyte.dev/amaranth/selfroles/templates"
)

func CategoryConstraints(currentlyEditingCategory *models.Category) discord.SectionComponent {
	text := templates.RenderTemplate("category_constraints", currentlyEditingCategory)
	return discord.NewSection(discord.NewTextDisplay(text)).
		WithAccessory(EditButton("/selfroles/1/constraints"))
}
