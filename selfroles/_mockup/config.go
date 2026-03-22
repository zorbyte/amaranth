package selfroles

import (
	"fmt"

	"github.com/disgoorg/disgo/discord"
)

const (
	titleLanding      string = "## 🔮 Self Roles Setup Wizard"
	titlePageCategory        = "## 📝 Category Editor"
)

var categoriesStub []Category

func baseConfigLayout(currentlyEditingCategory *Category) []discord.LayoutComponent {
	// TODO: Use a database function.
	categories := categoriesStub
	return []discord.LayoutComponent{
		discord.
			NewContainer(
				discord.NewTextDisplay(titleLanding),
			).
			AddComponents(
				categorySelector(categories, currentlyEditingCategory)...,
			).
			AddComponents(
				configFooter(categories, currentlyEditingCategory)...,
			),
	}
}

func categorySelector(
	categories []Category,
	currentlyEditingCategory *Category,
) []discord.ContainerSubComponent {
	isCurrentlyEditing := currentlyEditingCategory != nil

	placeholder := "Create, edit or delete a category..."
	if isCurrentlyEditing {
		placeholder = "Create, edit or delete another...."
	}

	var createNewCategoryOption = discord.
		NewStringSelectMenuOption("Create new...", "/selfroles/categories").
		WithEmoji(discord.NewComponentEmoji("🪄"))

	// Build a list of categories with the create new button at the top.
	categoryOptions := make([]discord.StringSelectMenuOption, 0, len(categories))
	categoryOptions = append(categoryOptions, createNewCategoryOption)
	for _, cat := range categories {
		catOpt := discord.NewStringSelectMenuOption(cat.Name, cat.ComponentID())
		if cat.Emoji != nil {
			catOpt = catOpt.WithEmoji(*cat.Emoji)
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
		"/selfroles/categories",
		placeholder,
		categoryOptions...,
	)

	return []discord.ContainerSubComponent{
		discord.NewLargeSeparator(),
		discord.NewActionRow(menu),
		discord.NewLargeSeparator(),
	}
}

func configFooter(
	categories []Category,
	currentlyEditingCategory *Category,
) []discord.ContainerSubComponent {
	var footerText string
	var totalCategories, totalRoles int
	if currentlyEditingCategory != nil {
		totalRoles = len(currentlyEditingCategory.Roles)
		footerText = fmt.Sprintf("%d Roles", totalRoles)
	} else {
		totalCategories = len(categories)
		for _, cat := range categories {
			totalRoles += len(cat.Roles)
		}

		footerText = fmt.Sprintf("%d Roles | %d Categories", totalRoles, totalCategories)
	}

	return []discord.ContainerSubComponent{
		discord.NewSmallSeparator().WithDivider(false),
		discord.NewTextDisplay(footerText),
	}
}
