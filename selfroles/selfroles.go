package selfroles

import (
	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/disgo/handler"
	"zorbyte.dev/amaranth/amaranth"
	"zorbyte.dev/amaranth/amaranth/middleware"
)

var command = discord.SlashCommandCreate{
	Name:        "selfroles",
	Description: "Self Roles Configurator",
	Options:     []discord.ApplicationCommandOption{},
}

func Load(a *amaranth.Amaranth) {
	//db := srmodels.NewDB(a)

	a.Router.Route("/selfroles", func(r handler.Router) {
		// r.Use(middleware.MustHaveAdminRole(a))
		// r.SlashCommand("/selfroles")
		r.Group(func(r handler.Router) {
			r.Use(middleware.MustHaveAdminRole(a))
			r.Command("/bulletin", bulletinMessageCommand(a))
			r.Route("/config", func(r handler.Router) {
				//r.Command("/", configCommandLanding(a))
				//r.Command("/{category_id}/delete", configCommand(a))
				// r.Command("/{category_id}/delete", configCommand(a))
				// r.SelectMenuComponent("/{menu_selection}")
			})
		})
	})

	a.RegisterCommands(command)
}

// /roles/<guild_id>/roles/create
func createRoleCommand() {}

// /roles/<guild_id>/roles/<role_id>/edit
func editRoleCommand() {}

// /roles/<guild_id>/roles/<role_id>/delete
func deleteRoleCommand() {}

// /roles/<guild_id>/categories/create
func createCategory() {}

// /roles/<guild_id>/categories/<category_id>/edit
func editCategory() {}

// /roles/<guild_id>/categories/<category_id>/delete
func deleteCategory() {}
