package components

import (
	"fmt"

	"github.com/disgoorg/disgo/discord"
	srmodels "zorbyte.dev/amaranth/selfroles/models"
)

func ConfigFooter(
	categories []srmodels.Category,
	currentlyEditingCategory *srmodels.Category,
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
