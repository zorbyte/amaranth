package selfroles

import (
	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/disgo/handler"
	"github.com/disgoorg/snowflake/v2"
	"zorbyte.dev/amaranth/amaranth"
)

// TODO: Custom router to route option IDs and stuff like that
// if the disgo option is not adequate.
// We'd then treat the components like a template view that we'd build.

// TODO: Idea for the real thing, you'll do a menu and you go through the sub menus through the drop down,
// there'll be a thing about unsaved changes added to the description etc.

const zeroWidthSpace string = "​"

var addRoleCommand = discord.SlashCommandCreate{
	Name:        "config",
	Description: "Configure Self Roles Stuff",
	Options: []discord.ApplicationCommandOption{
		discord.ApplicationCommandOptionSubCommand{
			Name:        "bulletin",
			Description: "Creates the bulletin message with the button to open the self roles picker.",
		},
	},
}

// type ConfiguratorMenu struct {
// 	pageNumber            int
// 	guildRoleCategories   []*Category
// 	categorisedRolesCache map[int]*SelectableRole
// }

func configCommandLanding(a *amaranth.Amaranth) handler.CommandHandler {
	command.Options = append(command.Options, discord.ApplicationCommandOptionSubCommand{
		Name:        "config",
		Description: "Configures the thing.",
	})

	return func(e *handler.CommandEvent) error {
		return e.CreateMessage(discord.NewMessageCreateV2(
			//buildConfigLayout(),
			categoryLayout()...,
		).ClearAllowedMentions())
	}
}

func categoryLayout() []discord.LayoutComponent {
	msgEmoji := discord.EmojiMention(snowflake.ID(1476911495454658580), "message")
	return []discord.LayoutComponent{
		discord.NewContainer(
			discord.NewTextDisplay("## 📝 Category Editor"),
			discord.NewLargeSeparator(),
			discord.NewActionRow(
				discord.NewStringSelectMenu(
					"/selfroles/action",
					"Create, edit or delete another...",
					createNewCategoryOption,
					discord.NewStringSelectMenuOption("Role Colours", "1").
						WithEmoji(discord.NewComponentEmoji("🎨")).
						WithDescription("Currently editing..."),
				),
			),
			discord.NewLargeSeparator(),
			discord.NewSection(
				discord.NewTextDisplayf(
					`**Details:**
- %s Emoji: %s
- %s Name: %s
- %s Accent Colour (*previewed on left*): %s
- %s Title: %s
- %s Description: %s`,
					msgEmoji, "`🎨`",
					msgEmoji, "`Role Colours`",
					msgEmoji, "`#1CB48F`",
					msgEmoji, "`What role colour would you like?`",
					msgEmoji, "`Some information about these roles may go here.`",
					// TODO: Would dynamically adapt to use a codeblock if it detects any `\n` in the description
					// data.
					//msgEmoji, "\n```\nAn example of a description that has:\nmultiple lines.```",
				),
			).WithAccessory(
				discord.NewPrimaryButton(zeroWidthSpace+"   Edit", "/selfroles/1/details").
					WithEmoji(discord.NewCustomComponentEmoji(snowflake.ID(1476927053004668999))),
			),
			discord.NewLargeSeparator(),
			discord.NewTextDisplay("**Nominated Roles:**"),
			discord.NewActionRow(
				discord.NewRoleSelectMenu("/selfroles/1/roles", "").
					//WithMinValues(1).
					WithMaxValues(20).
					SetDefaultValues(
						snowflake.MustParse("1115321748141916231"), snowflake.MustParse("1115818346051539104")),
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
				discord.NewPrimaryButton(zeroWidthSpace+"   Edit", "/selfroles/1/constraints").
					WithEmoji(discord.NewCustomComponentEmoji(snowflake.ID(1476927053004668999))),
			),
			//discord.NewLargeSeparator().WithDivider(false),
			//discord.NewTextDisplay("\n\n\n\nPlease select an option.\n\n\n\n"),
			discord.NewSmallSeparator().WithDivider(false),
			discord.NewTextDisplay("-# 2 Roles"),
		).WithAccentColor(0x1CB48F),
		discord.NewActionRow(
			discord.NewPrimaryButton(zeroWidthSpace+"   Back", "/selfroles/landing").
				WithEmoji(discord.NewComponentEmoji("⬅️")),
			discord.NewDangerButton(zeroWidthSpace+"   Delete", "/selfroles/1/delete").
				WithEmoji(discord.NewComponentEmoji("🗑️")),
		),
	}
}

func buildConfigLayout() discord.LayoutComponent {
	return discord.NewContainer(
		discord.NewTextDisplay("## 🔮 Self Roles Setup Wizard"),
		discord.NewLargeSeparator(),
		discord.NewActionRow(
			discord.NewStringSelectMenu(
				// /selfroles/action/{page_number}
				"/selfroles/action/1",
				"Create, edit or delete a category...",
				discord.NewStringSelectMenuOption("Create new...", "new").
					WithEmoji(discord.NewComponentEmoji("🪄")),
				discord.NewStringSelectMenuOption("Role Colours", "/selfroles/1").
					WithDescription("67 Roles").
					WithEmoji(discord.NewComponentEmoji("🎨")),
				// discord.NewStringSelectMenuOption("More...", "new").
				// 	WithDescription("Page 1 of 20").
				// 	WithEmoji(discord.NewComponentEmoji("➡️")),
			),
		),
		discord.NewLargeSeparator(),
		discord.NewTextDisplay("**Role Selector Channel:**"),
		discord.NewActionRow(
			discord.NewChannelSelectMenu("/selfroles/channel", "").
				//WithMinValues(1).
				WithMaxValues(1).
				WithChannelTypes(discord.ChannelTypeGuildText).
				AddDefaultValue(snowflake.MustParse("1117371780533850202")),
		),
		//discord.NewLargeSeparator().WithDivider(false),
		//discord.NewTextDisplay("\n\n\n\nPlease select an option.\n\n\n\n"),
		discord.NewSmallSeparator().WithDivider(false),
		discord.NewTextDisplay("-# 420 Roles | 69 Categories"),
	)
}

type MenuBodyState int

const (
	MenuBodyStateLanding MenuBodyState = iota
	MenuBodyStateCreate
	MenuBodyStateEditing
)

func mapIntoSnowflakes(values ...string) []snowflake.ID {
	snowflakes := make([]snowflake.ID, len(values))
	for i, s := range values {
		snowflakes[i] = snowflake.MustParse(s)
	}

	return snowflakes
}

func buildMenuBody(state MenuBodyState) {

}
