package cmd

import (
	"fmt"

	"../lib"
	homedir "github.com/mitchellh/go-homedir"
	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

var cfgFile string
var username string
var secret string
var zenbootUrl string
var id int
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
	RootCmd.PersistentFlags().StringVarP(&secret, "secret", "s", "", "The password to connect (default is empty)")
	RootCmd.PersistentFlags().StringVar(&cfgFile, "config", "", "config file (default is $HOME/.zenboot.json)")
	RootCmd.PersistentFlags().IntVarP(&id, "executionzone", "e", 0, "the id of the Execution Zone in which to execute.")
	initConfig()

	if zenbootUrl == "" {
		viper.SetDefault("zenbooturl", default_zenbootUrl)
		zenbootUrl = viper.GetString("zenbooturl")
	}
	if username == "" {
		username = viper.GetString("username")
	}
	if secret == "" {
		secret = viper.GetString("secret")
	}
	viper.SetDefault("author", "RPI <rpi@sap.com>")
	viper.SetDefault("license", "apache")
}

func initConfig() {
	if cfgFile != "" {
		viper.SetConfigFile(cfgFile)
	} else {
		home, err := homedir.Dir()
		lib.HandleError(err)

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
