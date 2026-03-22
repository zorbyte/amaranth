package amaranth

import (
	"log/slog"
	"os"
	"time"

	"github.com/Marlliton/slogpretty"
)

func CreateLogger(level slog.Level, devMode bool) *slog.Logger {
	var handler slog.Handler
	if devMode {
		handler = slogpretty.New(os.Stdout, &slogpretty.Options{
			Level:      level,
			AddSource:  true,
			Colorful:   true,
			Multiline:  true,
			TimeFormat: time.Stamp,
		})
	} else {
		handler = slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{
			Level:     level,
			AddSource: true,
		})
	}

	return slog.New(handler)
}
