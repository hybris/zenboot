package cmd

import (
	"fmt"
	"log"

	"../lib"
	"github.com/spf13/cobra"
)

func init() {
	RootCmd.AddCommand(callCmd)
}

var callCmd = &cobra.Command{
	Use:   "call [rest-command]",
	Short: "Test a rest-command with zenboot",
	Run: func(cmd *cobra.Command, args []string) {
		if len(args) < 1 {
			log.Fatalln("This command needs at least a rest-command to be passed.")
		}
		rest_call := args[0]

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret, Ignore: ignore}

		content, err := rest.SendGet(rest_call)
		lib.HandleError(err)

		fmt.Printf("%s\n", content)
	},
}
