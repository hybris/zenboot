package cmd

import (
	"fmt"
	"strconv"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

func init() {
	listCmd.AddCommand(listParametersCmd)
}

var listParametersCmd = &cobra.Command{
	Use:   "parameters [flags] [action]",
	Short: "list all required parameters of an Execution Zone or one of its actions",
	Run: func(cmd *cobra.Command, args []string) {
		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret, Ignore: ignore}
		var url = ""

		if len(args) < 1 {
			url = "executionzones/" + strconv.Itoa(id) + "/params/list"
		} else {
			action, _ := lib.ValidateAction(args[0])
			url = "executionzones/" + strconv.Itoa(id) + "/actions/" + action + "/params/list"
		}

		content, err := rest.SendGet(url)
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
