package amaranth

import (
	"slices"

	"github.com/disgoorg/snowflake/v2"
)

func (a *Amaranth) MemberHasModRole(memberRoleIDs []snowflake.ID) bool {
	modRoleIDs := slices.Concat(a.Config.Bot.Administrators, a.Config.Bot.Moderators)
	return roleIDsHaveIntersection(memberRoleIDs, modRoleIDs)
}

func (a *Amaranth) MemberHasAdminRole(memberRoleIDs []snowflake.ID) bool {
	return roleIDsHaveIntersection(memberRoleIDs, a.Config.Bot.Administrators)
}

// we want to see if set A has at least one member of set B.
// This can be a slow as fuck operation if we take the naive
// nested for loop approach. Lets make it faster!
// should be linear time of some form
func roleIDsHaveIntersection(
	roleIDsA []snowflake.ID,
	roleIDsB []snowflake.ID,
) bool {
	// the map should be the biggest object,
	// it is the fastest to look items up in..
	roleIDsToTurnIntoMap := roleIDsA
	roleIDsToIterate := roleIDsB
	if len(roleIDsA) < len(roleIDsB) {
		roleIDsToTurnIntoMap = roleIDsB
		roleIDsToIterate = roleIDsA
	}

	roleIDsMap := make(map[snowflake.ID]struct{}, len(roleIDsToTurnIntoMap))
	for _, roleID := range roleIDsToTurnIntoMap {
		roleIDsMap[roleID] = struct{}{}
	}

	for _, roleID := range roleIDsToIterate {
		_, ok := roleIDsMap[roleID]
		if ok {
			return true
		}
	}

	return false
}
