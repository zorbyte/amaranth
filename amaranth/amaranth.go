package amaranth

import (
	"context"
	"log/slog"
	"os"
	"time"

	"github.com/patrickmn/go-cache"

	"github.com/disgoorg/disgo/bot"
	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/disgo/handler"
	"github.com/uptrace/bun"
)

type Amaranth struct {
	shouldSyncDBTables   bool
	shouldSyncCommands   bool
	appCmdCreatePayloads []discord.ApplicationCommandCreate

	DB     *bun.DB
	Client *bot.Client
	Router handler.Router

	InteractionsCache *cache.Cache

	Log *slog.Logger

	Config  Config
	Version string
}

func New(
	version string,
	shouldSyncDBTables,
	shouldSyncCommands bool,
) *Amaranth {
	config, err := LoadConfig()
	if err != nil {
		slog.Error("Failed to load config", "error", err)
		os.Exit(1)
	}

	defaultLogger := CreateLogger(config.LogLevel, config.DevMode)
	slog.SetDefault(defaultLogger)

	log := defaultLogger.With("from", "amaranth")

	db, err := connectDatabase(config.DevMode, config.Database)
	if err != nil {
		log.Error("Failed to connect to database", "error", err)
		os.Exit(1)
	}

	a := &Amaranth{
		shouldSyncDBTables: shouldSyncDBTables,
		shouldSyncCommands: shouldSyncCommands,

		DB:     db,
		Router: handler.New(),

		InteractionsCache: cache.New(10*time.Minute, 20*time.Minute),

		Log: log,

		Config:  *config,
		Version: version,
	}

	if err = a.setupDiscordClient(); err != nil {
		slog.Error("Failed to start bot.", "error", err)
		os.Exit(1)
	}

	return a
}

func (a *Amaranth) Cleanup() {
	a.Log.Info("Shutting down...")
	a.Client.Close(context.TODO())
	a.DB.Close()
	if err := SaveConfig(a.Config); err != nil {
		a.Log.Error("Failed to save config.", "error", err)
	}
}
