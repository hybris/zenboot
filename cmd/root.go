package cmd

import (
//  "fmt"
//  "os"

//  homedir "github.com/mitchellh/go-homedir"
  "github.com/spf13/cobra"
  "github.com/spf13/viper"
)

var RootCmd = &cobra.Command{
  Use:   "zenboot",
  Short: "zenboot CLI is the fastest way for you to manage your environment",
  Long: `No matter if to manage your environment, administrate
                customer machines or just receive updates about current tasks,
                the zenboot CLI will assist and make it easier.`,
  Run: func(cmd *cobra.Command, args []string) {
    // Do Stuff Here
  },
}

func init() {
  viper.SetDefault("author", "RPI <rpi@sap.com>")
  viper.SetDefault("license", "apache")
}

func Execute() {
  RootCmd.Execute()
}

