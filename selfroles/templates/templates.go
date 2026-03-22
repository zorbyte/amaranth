package templates

import (
	"embed"
	_ "embed"
	"log/slog"
	"strings"
	"text/template"

	"zorbyte.dev/amaranth/amaranth/ui"
	"zorbyte.dev/amaranth/selfroles/models"
)

//go:embed *.go.tpl
var templatesFS embed.FS

var templateFns = template.FuncMap{
	"add": func(a, b int) int {
		return a + b
	},
	"sub": func(a, b int) int {
		return a - b
	},
}

var templates = template.Must(template.ParseFS(templatesFS)).Funcs(templateFns)

var memoisedUIEmojis = ui.NewEmojiTemplateData()

type templateExecutionData struct {
	UIEmojis ui.EmojiTemplateData
	Category *models.Category
}

func RenderTemplate(templateName string, category *models.Category) string {
	execData := templateExecutionData{
		UIEmojis: memoisedUIEmojis,
		Category: category,
	}

	var output strings.Builder
	if err := templates.ExecuteTemplate(&output, templateName, execData); err != nil {
		// TODO: Error reports channel plus unified error messages via ui package.
		slog.Error("Error executing template", "tpl", templateName, "error", err)
		output.WriteString(
			"\n\n" +
				"-# An error occurred while rendering the above output, it may be incomplete or have errors. " +
				"Please report this if it persists.",
		)

		return output.String() /*, fmt.Errorf("Failed to execute template `%s`: %w", templateName, err) */
	}

	return output.String()
}

// func executeDetailsTmpl(templateName string, category *srmodels.Category) string {
// 	ctx := templateExecutionData{
// 		UIEmojis: ui.EmojiTemplateData{},

// 		CategoryName:        category.Name,
// 		CategoryDisplayName: category.DisplayName,
// 		CategoryDescription: category.Description,
// 	}

// 	var output strings.Builder
// 	if err := selfRolesConfTpl.ExecuteTemplate(&output, templateName, ctx); err != nil {
// 		// TODO: Error reports channel plus unified template management and logging.
// 		slog.Error("Error executing template", "tmpl", "edit_page", "error", err)
// 		return "An error occurred while loading category details, please report this."
// 	}

// 	return output.String()
// }
