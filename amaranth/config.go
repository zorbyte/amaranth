package amaranth

import (
	"encoding/json"
	"errors"
	"log/slog"
	"os"
)

const configLocation string = "config.json"

type Config struct {
	DevMode         bool       `json:"dev_mode"`
	LogLevel        slog.Level `json:"log_level"`
	Bot             BotConfig  `json:"bot"`
	Database        DBConfig   `json:"database"`
}

func LoadConfig() (*Config, error) {
	file, err := os.Open(configLocation)
	if os.IsNotExist(err) {
		if file, err = os.Create(configLocation); err != nil {
			return nil, err
		}

		var data []byte
		if data, err = json.Marshal(Config{}); err != nil {
			return nil, err
		}

		if _, err = file.Write(data); err != nil {
			return nil, err
		}

		return nil, errors.New("config.json not found, created new one")
	} else if err != nil {
		return nil, err
	}

	var cfg Config
	if err = json.NewDecoder(file).Decode(&cfg); err != nil {
		return nil, err
	}

	return &cfg, nil
}

func SaveConfig(config Config) error {
	file, err := os.OpenFile(configLocation, os.O_WRONLY|os.O_CREATE|os.O_TRUNC, 0644)
	if err != nil {
		return err
	}

	defer func() {
		_ = file.Sync()
		_ = file.Close()
	}()

	data, err := json.MarshalIndent(config, "", "  ")
	if err != nil {
		return err
	}

	_, err = file.Write(data)
	return err
}
