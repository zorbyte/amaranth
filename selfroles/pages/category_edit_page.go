package pages

import (
	_ "embed"

	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/snowflake/v2"
	"zorbyte.dev/amaranth/amaranth"
	"zorbyte.dev/amaranth/selfroles/components"
	"zorbyte.dev/amaranth/selfroles/templates"
	"zorbyte.dev/amaranth/selfroles/models"
)

const EditPageRoute string = "/selfroles/config/edit"

func EditPage(a *amaranth.Amaranth, category models.Category) []discord.ContainerSubComponent {
	detailsText := templates.RenderTemplate("category_details", &category)

	return []discord.ContainerSubComponent{
		discord.NewSection(discord.NewTextDisplay(detailsText)).
			WithAccessory(components.EditButton("/selfroles/1/details")),
		discord.NewLargeSeparator(),
		discord.NewTextDisplay("**Nominated Roles:**"),
		discord.NewActionRow(
			discord.NewRoleSelectMenu("/selfroles/1/roles", "").
				//WithMinValues(1).
				WithMaxValues(20).
				SetDefaultValues(category.RoleIDs()...),
		),
		discord.NewTextDisplay("**Preview & Decorations:**"),
		discord.NewActionRow(
			discord.NewStringSelectMenu(
				"/selfroles/1/preview", "Select a role to decorate it...",
				discord.NewStringSelectMenuOption("Primrose", "/selfroles/1/1115321748141916231/decorate"),
				discord.NewStringSelectMenuOption("Moonstone", "/selfroles/1/1115818346051539104/decorate").
					WithDescription("An example of a decoration applied.").
					WithEmoji(discord.NewCustomComponentEmoji(snowflake.ID(1118338768651436152))),
			),
		),
		discord.NewLargeSeparator(),
		discord.NewSection(
			discord.NewTextDisplayf(
				`**Constraints:**
- %s Permits Multiple Selections
- %s Requires Roles: %s`, // TODO: We'd do a comma separated list i.e. @member, @jokester, @whatever
				// after 5 (or so), we'd have the 6th entry be "(32 more...)", to which the user
				// can see those in the edit menu if they truly desire.
				discord.EmojiMention(snowflake.ID(1476911324444229784), "cross"),
				discord.EmojiMention(snowflake.ID(1476922599916441722), "role"),
				discord.RoleMention(snowflake.ID(1116637789379907655)),
			),
		).WithAccessory(
			components.EditButton("/selfroles/1/constraints"),
		),
	}
}
