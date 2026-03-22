package ui

import (
	"errors"

	"github.com/disgoorg/disgo/discord"
	"github.com/disgoorg/snowflake/v2"
	"github.com/forPelevin/gomoji"
)

var (
	EditEmoji    = NewEmoji(1476927053004668999, "edit", false)
	MessageEmoji = NewEmoji(1476911495454658580, "message", false)
	TickEmoji    = NewEmoji(1476911636475547668, "tick", false)
	CrossEmoji   = NewEmoji(1476911324444229784, "cross", false)
	RoleEmoji    = NewEmoji(1476922599916441722, "role", false)
)

type Emoji struct {
	ID       snowflake.ID `json:"id,omitempty"`
	Name     string       `json:"name,omitempty"`
	Animated bool         `json:"animated,omitempty"`
}

var NoEmojiInStringError = errors.New("no emoji found in string")

// ParseEmoji will parse an emoji from a given string.
// It is a forgiving parser such that it will extract the first valid instance
// of an emoji from the string `text`, if no such instances are found,
// returns nil alongside a NoEmojiInStringError (joined with any other relevant error)
func ParseEmoji(text string) (*Emoji, error) {
	emojiMention := discord.MentionTypeEmoji.FindStringSubmatch(text)
	if emojiMention != nil {
		ID, err := snowflake.Parse(emojiMention[1])
		if err != nil {
			return nil, errors.Join(NoEmojiInStringError, err)
		}

		return &Emoji{
			ID:       ID,
			Name:     emojiMention[2],
			Animated: emojiMention[0][:2] == "<a",
		}, nil
	}

	unicodeEmojis := gomoji.FindAll(text)
	if unicodeEmojis == nil {
		return nil, NoEmojiInStringError
	}

	return &Emoji{Name: unicodeEmojis[0].Character}, nil
}

func NewEmoji(ID uint64, name string, animated bool) Emoji {
	return Emoji{snowflake.ID(ID), name, animated}
}

func (e *Emoji) ComponentEmoji() discord.ComponentEmoji {
	return discord.ComponentEmoji{ID: e.ID, Name: e.Name, Animated: e.Animated}
}

func (e *Emoji) Mention() string {
	// no emoji present.
	if e == nil {
		return ""
	}

	if e.ID != 0 && e.Name != "" {
		var mention string
		if e.Animated {
			mention = discord.AnimatedEmojiMention(e.ID, e.Name)
		} else {
			mention = discord.EmojiMention(e.ID, e.Name)
		}

		return mention
	}

	// Vanilla unicode emoji.
	return e.Name
}

func EmojiFromComponentEmoji(cemoji *discord.ComponentEmoji) Emoji {
	return Emoji{ID: cemoji.ID, Name: cemoji.Name, Animated: cemoji.Animated}
}
