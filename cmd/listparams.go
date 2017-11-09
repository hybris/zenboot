package cmd

import (
	"fmt"
	"log"
	"strconv"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

func init() {
	listCmd.AddCommand(listParamsCmd)
}

var listParamsCmd = &cobra.Command{
	Use:   "params [flags] [action]",
	Short: "list all required parameters of an Execution Zone action",
	Run: func(cmd *cobra.Command, args []string) {
		if len(args) < 1 {
			log.Fatalln("Please specify an action to list parameters for.")
		}

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret}

		action := args[0]

		content, err := rest.SendGet("executionzones/" + strconv.Itoa(id) + "/actions/" + action + "/listparams")
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
