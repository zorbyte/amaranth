package main

import (
	"flag"
	"os"
	"os/signal"
	"runtime/debug"
	"syscall"

	_ "github.com/joho/godotenv/autoload"

	"zorbyte.dev/amaranth/amaranth"
	// "zorbyte.dev/amaranth/selfroles"
)

var (
	version = "development"
)

var (
	shouldSyncDBTables *bool
	shouldSyncCommands *bool
)

func init() {
	shouldSyncDBTables = flag.Bool("sync-db", false, "Whether to sync the database tables")
	shouldSyncCommands = flag.Bool("sync-cmds", false, "Whether to sync the commands")
	flag.Parse()

	bldInf, ok := debug.ReadBuildInfo()
	if !ok {
		return
	}

	version = bldInf.Main.Version
}

func main() {
	a := amaranth.New(version, *shouldSyncDBTables, *shouldSyncCommands)
	defer a.Cleanup()
	// loadModules(a)
	a.Login()

	a.Log.Info("Client is running. Press CTRL-C to exit.")
	s := make(chan os.Signal, 1)
	signal.Notify(s, syscall.SIGINT, syscall.SIGTERM, os.Interrupt)
	<-s
}

// func loadModules(a *amaranth.Amaranth) {
// 	selfroles.Load(a)
// }
