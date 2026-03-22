package models

import (
	"github.com/disgoorg/snowflake/v2"
	"github.com/uptrace/bun"
	"zorbyte.dev/amaranth/amaranth/ui"
)

type Role struct {
	bun.BaseModel `bun:"table:sr_roles"`

	/**
	 * Composite_PK(Category_ID, Role_ID)
	 * Index(Guild_ID, Role_ID)
	 */

	GuildID    snowflake.ID `bun:",notnull"`
	RoleID     snowflake.ID `bun:",pk"`
	CategoryID uint64       `bun:",pk"`

	Category *Category `bun:"rel:belongs-to,join:category_id=id"`

	Name        string    `bun:",notnull"`
	Description string    `bun:",nullzero"`
	Emoji       *ui.Emoji `bun:",nullable,type:json"`

	/* Constraints */

	RequiresTicket  bool           `bun:",notnull"`
	RequiredRoleIDs []snowflake.ID `bun:",nullzero,nullable,type:json"`
}

type RolesDB interface {
	GetRole(roleID snowflake.ID) (Role, error)
	GetRoles() ([]Role, error)
	GetRoleIDs() ([]snowflake.ID, error)
	CreateRole(roleID snowflake.ID, name string) error
	BulkCreateRoles([]Role) error
	SetRoleDecorations(roleID snowflake.ID, name, description string, emoji *ui.Emoji) error
	SetRoleConstraints(roleID snowflake.ID, requiresTicket bool, requiredRoleIDs []snowflake.ID) error
	DeleteRole(roleID snowflake.ID) error
}

func (s *selfRolesDB) GetRole(roleID snowflake.ID) (role Role, err error) {
	err = s.db.NewSelect().
		Conn(s.conn).
		Model(&role).
		Where("role_id = ? AND category_id = ?", roleID, s.categoryID). //NOSONAR
		Scan(s.ctx)

	return
}

func (s *selfRolesDB) GetRoles() (roles []Role, err error) {
	err = s.db.NewSelect().
		Conn(s.conn).
		Model(&roles).
		Where("guild_id = ? AND category_id = ?", s.guildID, s.categoryID). //NOSONAR
		Scan(s.ctx)

	return
}

func (s *selfRolesDB) GetRoleIDs() (roleIDs []snowflake.ID, err error) {
	err = s.db.NewSelect().
		Conn(s.conn).
		Model((*Role)(nil)).
		Column("role_id").
		Where("guild_id = ? AND category_id = ?", s.guildID, s.categoryID).
		Scan(s.ctx, &roleIDs)

	return
}

func (s *selfRolesDB) CreateRole(roleID snowflake.ID, name string) error {
	selectableRole := &Role{
		GuildID: s.guildID,

		CategoryID: s.categoryID,
		RoleID:     roleID,

		Name: name,
	}

	_, err := s.db.NewInsert().Conn(s.conn).Model(selectableRole).Exec(s.ctx)
	return err
}

func (s *selfRolesDB) BulkCreateRoles(roles []Role) error {
	_, err := s.db.NewInsert().
		Conn(s.conn).
		Model(&roles).
		Value("guild_id", "?", s.guildID).
		Value("category_id", "?", s.categoryID).
		Exec(s.ctx)

	return err
}

func (s *selfRolesDB) SetRoleDecorations(
	roleID snowflake.ID,
	name,
	description string,
	emoji *ui.Emoji,
) error {
	query := s.db.NewUpdate().
		Conn(s.conn).
		Model((*Role)(nil)).
		Where("guild_id = ? AND category_id = ?", s.guildID, s.categoryID).
		Set("option_name = ?, option_description = ?", name, description)

	if emoji != nil {
		query.Set("emoji = ?", emoji)
	}

	_, err := query.Exec(s.ctx)

	return err
}

func (s *selfRolesDB) SetRoleConstraints(
	roleID snowflake.ID,
	requiresTicket bool,
	requiredRoleIDs []snowflake.ID,
) error {
	_, err := s.db.NewUpdate().
		Conn(s.conn).
		Model((*Role)(nil)).
		Where("guild_id = ? AND category_id = ?", s.guildID, s.categoryID).
		Set("requires_ticket = ?, required_role_ids = ?", requiresTicket, requiredRoleIDs).
		Exec(s.ctx)

	return err
}

func (s *selfRolesDB) DeleteRole(roleID snowflake.ID) error {
	_, err := s.db.NewDelete().
		Conn(s.conn).
		Model((*Role)(nil)).
		Where("guild_id = ?", s.guildID).
		Where("category_id = ?", s.categoryID).Exec(s.ctx)

	return err
}
