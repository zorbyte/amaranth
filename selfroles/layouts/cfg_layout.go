package layouts

import (
	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/snowflake/v2"
	"zorbyte.dev/amaranth/amaranth"
	"zorbyte.dev/amaranth/selfroles/components"
	"zorbyte.dev/amaranth/selfroles/models"
	"zorbyte.dev/amaranth/selfroles/pages"
)

const (
	titleLanding      string = "## 🔮 Self Roles Setup Wizard"
	titlePageCategory        = "## 📝 Category Editor"
)

var categoriesStub []models.Category

func Layout(a *amaranth.Amaranth, guildID snowflake.ID, currentlyEditingCategory *models.Category) []discord.LayoutComponent {
	// TODO: Use a database function.
	categories := categoriesStub

	var body []discord.ContainerSubComponent

	if currentlyEditingCategory != nil {
		body = pages.EditPage(a, *currentlyEditingCategory)
	}

	return []discord.LayoutComponent{
		discord.
			NewContainer(
				discord.NewTextDisplay(titleLanding),
			).
			AddComponents(
				components.CategorySelector(guildID, categories, currentlyEditingCategory)...,
			).
			AddComponents(
				body...,
			).
			AddComponents(
				components.ConfigFooter(categories, currentlyEditingCategory)...,
			),
	}
}
