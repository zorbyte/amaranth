package models

import (
	"context"

	"github.com/disgoorg/snowflake/v2"
	"github.com/uptrace/bun"
	"zorbyte.dev/amaranth/amaranth"
)

type SelfRolesDB interface {
	WithContext(ctx context.Context) SelfRolesDB
	WithTransaction(ctx context.Context, tx *bun.Tx) SelfRolesDB
	WithoutTransaction() SelfRolesDB

	// WithGuildID(guildID snowflake.ID) SelfRolesDB
	// WithCategoryID(categoryID snowflake.ID) SelfRolesDB

	Roles(guildID snowflake.ID, categoryID uint64) RolesDB
	Categories(guildID snowflake.ID) CategoriesDB
}

type selfRolesDB struct {
	db *bun.DB

	conn bun.IConn
	ctx  context.Context

	// roleID,
	guildID    snowflake.ID
	categoryID uint64
}

func NewDB(ctx context.Context, a *amaranth.Amaranth) SelfRolesDB {
	a.SyncModel(ctx, (*Role)(nil), func(ctx context.Context, tx bun.Tx, model any) error {
		_, err := tx.NewCreateIndex().
			Model(model).
			Index("idx_guild_id_role_id").
			Column("guild_id", "role_id").
			Exec(ctx)

		return err
	})

	a.SyncModel(ctx, (*Category)(nil), nil)

	// might not be wise to recycle this context, need to consider implications (if any)...
	return &selfRolesDB{a.DB, a.DB, ctx, 0, 0}
}

func (s selfRolesDB) WithoutTransaction() SelfRolesDB {
	s.conn = s.db
	return &s
}

func (s selfRolesDB) WithTransaction(ctx context.Context, tx *bun.Tx) SelfRolesDB {
	s.ctx = ctx
	s.conn = tx

	return &s
}

func (s selfRolesDB) WithContext(ctx context.Context) SelfRolesDB {
	s.ctx = ctx
	return &s
}

func (s selfRolesDB) Roles(guildID snowflake.ID, categoryID uint64) RolesDB {
	s.guildID = guildID
	s.categoryID = categoryID

	return &s
}

func (s selfRolesDB) Categories(guildID snowflake.ID) CategoriesDB {
	// TODO: This code is here if we decide to remove the guildID parameter and
	// and use strictly builders. See the interface above.
	// if s.guildID == 0 {
	// 	// FIXME: Failing is usually a bad idea, but we're still prototyping.
	// 	panic("Cannot access categories resource while guildID is set to a zero value.")
	// }

	s.guildID = guildID

	return &s
}
