{{ $allowsMultipleEmoji := .UIEmojis.Cross -}}
{{ if .Category.AllowMultipleSelections -}}
  {{ $allowsMultipleEmoji = .UIEmojis.Tick -}}
{{ end -}}

**Constraints:**
- {{ $allowsMultipleEmoji }} Permits Multiple Selections
{{- /* 
     * after 5 roles, we'd have the 6th entry be "(32 more...)"
     * to avoid the list from becoming too big. the user can still
     * view these if they press the edit accessory button.
     */ -}}
- {{ $UIEmojis.Role }} Requires Roles:{{ " " }}
{{- $rolesSize := len .Category.RequiredRoleIDs }}
{{- $idx, $role range := .Category.RequiredRoleIDs -}}
  <@&{{ $role.ID }}>
  {{- if and (gt $idx 4) (eq $idx (sub $rolesLen 1)) -}}
    ({{ sub $rolesSize (add $idx 1) }} more...)
    {{- break }}
  {{- end }}
{{- end -}}