package models

import (
	"github.com/disgoorg/snowflake/v2"
	"github.com/uptrace/bun"
	"zorbyte.dev/amaranth/amaranth/ui"
)

type Category struct {
	bun.BaseModel `bun:"table:role_categories"`

	ID      uint64       `bun:",pk,autoincrement"`
	GuildID snowflake.ID `bun:",notnull"`

	Roles []Role `bun:"rel:has-many,join:id=category_id"`

	/* Option Display Details */

	// FIXME: Look into maximum option name length in Discord.
	Name  string    `bun:",notnull,type:varchar(120)"`
	Emoji *ui.Emoji `bun:",nullable,type:json"`

	/* Bulletin Displays */

	AccentColour uint32 `bun:",nullzero,nullzero"`
	Title        string `bun:",nullzero,nullable,type:varchar(120)"`
	Description  string `bun:",nullzero,nullable"`

	/* Constraints */

	AllowMultipleSelections bool           `bun:",notnull"`
	RequiredRoleIDs         []snowflake.ID `bun:",nullzero,nullable,type:json"`
}

func (c *Category) RoleIDs() []snowflake.ID {
	roleIDs := make([]snowflake.ID, len(c.Roles))
	for idx, role := range c.Roles {
		roleIDs[idx] = role.RoleID
	}

	return roleIDs
}

type CategoriesDB interface {
	GetCategory(categoryID uint64, withRoles bool) (Category, error)
	GetCategories(withRoles bool) ([]Category, error)
	CreateCategory(name /*title,*/, description string, allowMultipleSelections bool, requiredRoleIDs []snowflake.ID) error
	SetCategoryDecorations(categoryID uint64, name, title, description string, accentColour uint32, emoji *ui.Emoji) error
	SetCategoryConstraints(categoryID uint64, allowMultipleSelections bool, requiredRoleIDs []snowflake.ID) error
	DeleteCategory(categoryID uint64) error
}

func (s *selfRolesDB) GetCategory(categoryID uint64, withRoles bool) (category Category, err error) {
	query := s.db.NewSelect().
		Conn(s.conn).
		Model(&category).
		Where("category_id = ?", categoryID) //NOSONAR

	if withRoles {
		query = query.Relation("roles")
	}

	err = query.Scan(s.ctx)
	return
}

func (s *selfRolesDB) GetCategories(withRoles bool) (categories []Category, err error) {
	query := s.db.NewSelect().
		Conn(s.conn).
		Model(&categories).
		Where("guild_id = ?", s.guildID)

	if withRoles {
		query = query.Relation("roles")
	}

	err = query.Scan(s.ctx)
	return
}

func (s *selfRolesDB) CreateCategory(
	// TODO: Consider if title prompts in
	// the initial modal. This is a UX consideration
	// as users may feel compelled to input something
	// without getting a feel for the default functionality.
	name /*title,*/, description string,
	allowMultipleSelections bool,
	requiredRoleIDs []snowflake.ID,
) error {
	category := &Category{
		GuildID: s.guildID,

		Name: name,

		AllowMultipleSelections: allowMultipleSelections,
		RequiredRoleIDs:         requiredRoleIDs,
	}

	_, err := s.db.NewInsert().Conn(s.conn).Model(category).Exec(s.ctx)
	return err
}

func (s *selfRolesDB) SetCategoryDecorations(
	categoryID uint64,
	name, title, description string,
	accentColour uint32, emoji *ui.Emoji,
) error {
	query := s.db.NewUpdate().
		Model((*Category)(nil)).
		Where("category_id = ?", categoryID).
		Set(
			"name = ?, title = ?, description = ?, accent_colour = ?",
			name, title, description, accentColour,
		)

	if emoji != nil {
		query.Set("emoji = ?", emoji)
	}

	_, err := query.Exec(s.ctx)

	return err
}

func (s *selfRolesDB) SetCategoryConstraints(
	categoryID uint64,
	allowMultipleSelections bool,
	requiredRoleIDs []snowflake.ID,
) error {
	_, err := s.db.NewUpdate().
		Conn(s.conn).
		Model((*Category)(nil)).
		Where("category_id = ?", categoryID).
		Set("allow_multiple_selections = ?, required_role_ids = ?", allowMultipleSelections, requiredRoleIDs).
		Exec(s.ctx)

	return err
}

func (s *selfRolesDB) DeleteCategory(categoryID uint64) error {
	_, err := s.db.NewDelete().
		Conn(s.conn).
		Model((*Category)(nil)).
		Where("category_id = ?", categoryID).
		Exec(s.ctx)

	return err
}
