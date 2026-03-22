{{ $te := .UI.TextEmoji -}}
**Details:**
- {{ $te }} Emoji: `{{ .Category.Emoji }}`
- {{ $te }} Name: `{{ .Category.Name }}`
{{ if .Category.Colour -}}
  - {{ $te }} Accent Colour (*previewed on left*): `{{ .Category.Colour }}`
{{ else -}}
  - {{ $te }} Accent Colour: `N/A`
{{- end }}
- {{ $te }} Title: `{{ .Category.DisplayName }}`
- {{ $te }} Description: `{{ .Category.Description  }}`