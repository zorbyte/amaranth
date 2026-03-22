package components

import (
	"fmt"

	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/snowflake/v2"
	"zorbyte.dev/amaranth/selfroles/models"
	"zorbyte.dev/amaranth/selfroles/routes"
)

// const (
// 	categorySelectorComponentRoute srconfigurator.Route = configCommandRoute + "/categories"
// 	createNewCategoryOptionRoute                        = categorySelectorComponentRoute + "/new"
// )

// func selectCategoryToEditOptionRoute(categoryID int64) srconfigurator.Route {
// 	return Route(fmt.Sprintf("%s/%d", categorySelectorComponentRoute, categoryID))
// }

func CategorySelector(
	guildID snowflake.ID,
	categories []models.Category,
	currentlyEditingCategory *models.Category,
) []discord.ContainerSubComponent {
	var createNewCategoryOption = discord.
		NewStringSelectMenuOption("Create new...", routes.CreateNewCategoryTextDropdownOptionRoute.Format(guildID)).
		WithEmoji(discord.NewComponentEmoji("🪄"))

	isCurrentlyEditing := currentlyEditingCategory != nil

	placeholder := "Create, edit or delete a category..."
	if isCurrentlyEditing {
		placeholder = "Create, edit or delete another...."
	}

	// Build a list of categories with the create new button at the top.
	categoryOptions := make([]discord.StringSelectMenuOption, 0, len(categories))
	categoryOptions = append(categoryOptions, createNewCategoryOption)
	for _, cat := range categories {
		catOpt := discord.NewStringSelectMenuOption(
			cat.Name,
			routes.OpenCategoryEditorPageTextDropdownOptionRoute.Format(guildID, cat.ID),
		)

		if cat.Emoji != nil {
			catOpt = catOpt.WithEmoji(cat.Emoji.ComponentEmoji())
		}

		if isCurrentlyEditing && cat.ID == currentlyEditingCategory.ID {
			catOpt = catOpt.WithDescription("Currently editing...")
		} else if numRoles := len(cat.Roles); numRoles > 0 {
			// Number of roles will be shown in the footer if the category is actively
			// being edited, hence the `else if`
			catOpt = catOpt.WithDescription(fmt.Sprintf("%d Roles", numRoles))
		}

		categoryOptions = append(categoryOptions, catOpt)
	}

	menu := discord.NewStringSelectMenu(
		routes.CategoryTextDropdownRoute.Format(guildID),
		placeholder,
		categoryOptions...,
	)

	return []discord.ContainerSubComponent{
		discord.NewLargeSeparator(),
		discord.NewActionRow(menu),
		discord.NewLargeSeparator(),
	}
}
