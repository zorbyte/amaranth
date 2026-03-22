package ui

type EmojiTemplateData struct {
	MessageEmoji,
	TickEmoji,
	CrossEmoji,
	RoleEmoji string
}

func NewEmojiTemplateData() EmojiTemplateData {
	return EmojiTemplateData{
		MessageEmoji: MessageEmoji.Mention(),
		TickEmoji: TickEmoji.Mention(),
		CrossEmoji: CrossEmoji.Mention(),
		RoleEmoji: RoleEmoji.Mention(),
	}
}
