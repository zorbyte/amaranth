package middleware

import (
	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/disgo/handler"
	"zorbyte.dev/amaranth/amaranth"
)

func MustHaveAdminRole(a *amaranth.Amaranth) handler.Middleware {
	return func(next handler.Handler) handler.Handler {
		return func(e *handler.InteractionEvent) error {
			if !a.MemberHasAdminRole(e.Member().RoleIDs) {
				return e.CreateMessage(discord.
					NewMessageCreate().
					WithEphemeral(true).
					// TODO: Some embed instead.
					WithContent("You do not have permissions."),
				)
			}

			return next(e)
		}
	}
}
