package routes

import "fmt"

type Route string

const (
	/* Chat Input Command */
	StartWizardChatInputCommandRoute Route = "/selfroles/config"

	/* Landing Page Container */

	SetBulletinChannelDropdownRoute Route = "/selfroles/config/%s/channel"

	/* Global */

	// CategoryTextDropdownRoute
	CategoryTextDropdownRoute Route = "/selfroles/config/%s/categories"

	// CreateNewCategoryTextDropdownOptionRoute
	CreateNewCategoryTextDropdownOptionRoute Route = "/selfroles/config/%s/categories"

	// OpenCategoryEditorPageTextDropdownOptionRoute - guildID, categoryID
	OpenCategoryEditorPageTextDropdownOptionRoute Route = "/selfroles/config/%s/categories/%s" //NOSONAR

	/* Details Editor/Creator Modal  */

	// LaunchCategoryDetailsEditorButtonRoute
	LaunchCategoryDetailsEditorButtonRoute Route = "/selfroles/config/%s/categories/%s"

	// SaveCategoryDetailsModalRoute
	SaveCategoryDetailsModalSubmitRoute Route = "/selfroles/config/%s/categories/%s"

	/* Nominate Roles Dropdown */

	// SetCategoryRolesDropdownRoute needs to have its category ID inserted
	// via Route.Format()
	SetCategoryRolesRoleDropdownRoute Route = "/selfroles/config/%s/categories/%s/roles"

	/* Decorate Roles Dropdown + Modal  */

	// LaunchRoleDecoratorDropdownOptionRoute needs to have its category ID inserted
	// via Route.Format(). When the interaction is handled, the CustomID
	// for the role being edited to launch the modal will simply just be
	LaunchRoleDecoratorTextDropdownOptionRoute Route = "/selfroles/config/%s/categories/%s/roles"

	SaveRoleDecorationsModalSubmitRoute Route = "/selfroles/config/%s/categories/%s/roles/%s"
)

func (r Route) Format(opts ...any) string {
	return fmt.Sprintf(string(r), opts)
}

// func getPageFromRoute(
// 	route Route,
// 	categories []selfroles.Category,
// 	currentlyEditingCategory *selfroles.Category,
// ) {
// 	switch route {
// 	case categorySelectorRoute:
// 	}
// }
