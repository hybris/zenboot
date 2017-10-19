package cmd

import (
  "fmt"
  "os"

  homedir "github.com/mitchellh/go-homedir"
  "github.com/spf13/cobra"
  "github.com/spf13/viper"
)

var cfgFile string
var username string
var password string
var zenbootUrl string
var default_zenbootUrl string = "https://zenboot.hybris.com"

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
  RootCmd.PersistentFlags().StringVarP(&zenbootUrl, "zenbooturl", "z", "", "The zenboot instance to use (default is https://zenboot.hybris.com)")
  RootCmd.PersistentFlags().StringVarP(&username, "username", "u", "", "The username to connect to zenboot (default is empty)")
  RootCmd.PersistentFlags().StringVarP(&password, "password", "p", "", "The password to connect (default is empty)")
  RootCmd.PersistentFlags().StringVar(&cfgFile, "config", "", "config file (default is $HOME/.zenboot.json)")
  initConfig()

  if zenbootUrl == "" {
      viper.SetDefault("zenbooturl", default_zenbootUrl)
      zenbootUrl = viper.GetString("zenbooturl")
  }
  if username == "" {
      username = viper.GetString("username")
  }
  if password == "" {
      password = viper.GetString("password")
  }
  viper.SetDefault("author", "RPI <rpi@sap.com>")
  viper.SetDefault("license", "apache")
}

func initConfig() {
    if cfgFile != "" {
        viper.SetConfigFile(cfgFile)
    } else {
        home, err := homedir.Dir()
        if err != nil {
            fmt.Println(err)
            os.Exit(1)
        }

        viper.AddConfigPath(home)
        viper.SetConfigName(".zenboot")
    }

    if err := viper.ReadInConfig(); err != nil {
        fmt.Println("Can't find a config file: ", err)
    }
}

func Execute() {
  RootCmd.Execute()
}

