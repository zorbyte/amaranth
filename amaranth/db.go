package amaranth

import (
	"context"
	"database/sql"
	"fmt"
	"log/slog"
	"time"

	"github.com/uptrace/bun"
	"github.com/uptrace/bun/dialect/sqlitedialect"
	"github.com/uptrace/bun/driver/sqliteshim"
	"github.com/uptrace/bun/extra/bunslog"
)

type DBConfig struct {
	LogLevel slog.Level `json:"log_level"`
	Database string     `json:"database"`
	//Consider adding fields for postgres if we go that way.
}

func connectDatabase(devMode bool, config DBConfig) (*bun.DB, error) {
	var DSN = fmt.Sprintf("file:%s?cache=shared&mode=rwc", config.Database)

	log := CreateLogger(config.LogLevel, devMode).With("from", "db")
	log.Info("Connecting to Database", "DSN", DSN)

	sqlDB, err := sql.Open(sqliteshim.ShimName, DSN)
	if err != nil {
		return nil, err
	}

	// Create Bun database instance
	db := bun.NewDB(sqlDB, sqlitedialect.New())

	queryLogger := bunslog.NewQueryHook(
		bunslog.WithLogger(log),
		bunslog.WithQueryLogLevel(slog.LevelDebug),
		bunslog.WithSlowQueryLogLevel(slog.LevelWarn),
		bunslog.WithErrorQueryLogLevel(slog.LevelError),
		bunslog.WithSlowQueryThreshold(3*time.Second),
		bunslog.WithLogFormat(func(event *bun.QueryEvent) []slog.Attr {
			return []slog.Attr{
				slog.String("query", event.Query),
			}
		}),
	)

	db = db.WithQueryHook(queryLogger)

	return db, nil
}

func (a *Amaranth) SyncModel(
	ctx context.Context,
	model any,
	syncTasks func(ctx context.Context, tx bun.Tx, model any) error,
) error {
	if a.shouldSyncDBTables {
		return a.DB.RunInTx(ctx, nil, func(ctx context.Context, tx bun.Tx) error {
			if _, err := tx.NewCreateTable().
				Model(model).
				IfNotExists().
				Exec(ctx); err != nil {
				return err
			}

			select {
			case <-ctx.Done():
				return ctx.Err()
			default:
			}

			if syncTasks != nil {
				return syncTasks(ctx, tx, model)
			}

			return nil // commit to db
		})

	}

	return nil
}
