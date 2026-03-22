package amaranth

import (
	"context"
	"errors"
	"log/slog"
	"os"

	"github.com/disgoorg/disgo"
	"github.com/disgoorg/disgo/bot"
	"github.com/disgoorg/disgo/cache"
	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/disgo/events"
	"github.com/disgoorg/disgo/gateway"
	"github.com/disgoorg/disgo/handler"
	"github.com/disgoorg/disgo/handler/middleware"
	"github.com/disgoorg/snowflake/v2"
)

type BotConfig struct {
	LogLevel        slog.Level     `json:"log_level"`
	GatewayLogLevel slog.Level     `json:"gateway_log_level"`
	GuildID         snowflake.ID   `json:"guild_id"`
	Administrators  []snowflake.ID `json:"administrators"`
	Moderators      []snowflake.ID `json:"moderators"`
}

func (a *Amaranth) setupDiscordClient() (err error) {
	token, found := os.LookupEnv("BOT_TOKEN")
	if !found {
		err = errors.New("`BOT_TOKEN` environment variable was not set/found")
		return
	}

	a.Client, err = disgo.New(token,
		bot.WithLogger(CreateLogger(a.Config.Bot.LogLevel, a.Config.DevMode).With("from", "disgo")),
		bot.WithGatewayConfigOpts(
			gateway.WithIntents(
				gateway.IntentGuildMessages|
					gateway.IntentDirectMessages|
					gateway.IntentMessageContent,
			),
			gateway.WithCompression(gateway.CompressionZlibStream),
			gateway.WithPresenceOpts(
				gateway.WithCustomActivity("loading..."),
				gateway.WithOnlineStatus(discord.OnlineStatusDND),
			),
		),
		bot.WithCacheConfigOpts(cache.WithCaches(cache.FlagGuilds)),
		bot.WithEventListenerFunc(a.onReady),
		bot.WithEventListeners(a.Router),
		bot.WithGatewayConfigOpts(gateway.WithLogger(CreateLogger(a.Config.Bot.GatewayLogLevel, a.Config.DevMode))),
	)

	a.Router.Use(middleware.Logger)
	// FIXME: In this log, it is IMPERATIVE that the interaction name and custom_id
	// is logged so that we can troubleshoot the source of the error.
	a.Router.Use(func(next handler.Handler) handler.Handler {
		return func(e *handler.InteractionEvent) error {
			if err := next(e); err != nil {
				a.Log.Error("Error occurred while handling interaction.", "error", err)
			}

			return nil
		}
	})

	return
}

func (a *Amaranth) syncCommands(commands []discord.ApplicationCommandCreate, guildIDs ...snowflake.ID) {
	if len(guildIDs) == 0 {
		if _, err := a.Client.Rest.SetGlobalCommands(a.Client.ApplicationID, commands); err != nil {
			a.Log.Error("Failed to sync commands.", "error", err)
		}

		return
	}

	for _, guildID := range guildIDs {
		if _, err := a.Client.Rest.SetGuildCommands(a.Client.ApplicationID, guildID, commands); err != nil {
			a.Log.Error("Failed to sync commands.", "error", err)
		}
	}
}

func (a *Amaranth) onReady(_ *events.Ready) {
	a.Log.Info("Amaranth ready")
	if err := a.Client.SetPresence(context.TODO(),
		gateway.WithCustomActivity("under development"),
		gateway.WithOnlineStatus(discord.OnlineStatusDND),
	); err != nil {
		a.Log.Error("Failed to set presence.", "error", err)
	}
}

func (a *Amaranth) RegisterCommands(commands ...discord.ApplicationCommandCreate) {
	a.appCmdCreatePayloads = append(a.appCmdCreatePayloads, commands...)
}

// Login to Discord and maintain a websocket connection.
// This defers to a goroutine, so it is important to find a way
// to block your program after calling this.
func (a *Amaranth) Login() {
	if a.shouldSyncCommands {
		var guildIDs []snowflake.ID
		if a.Config.DevMode {
			a.Log.Info(
				"Bot started in dev mode, syncing commands with bot guild",
				"guild_id", a.Config.Bot.GuildID,
			)

			guildIDs = append(guildIDs, a.Config.Bot.GuildID)
		}

		a.syncCommands(a.appCmdCreatePayloads, guildIDs...)
	}

	if err := a.Client.OpenGateway(context.TODO()); err != nil {
		a.Log.Error("Failed to connect to gateway", "error", err)
	}
}
