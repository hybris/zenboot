package cmd

import (
	"fmt"
	"strconv"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

func init() {
	listCmd.AddCommand(listActionsCmd)
}

var listActionsCmd = &cobra.Command{
	Use:   "actions [flags]",
	Short: "list all action names of the specific Execution Zone",
	Run: func(cmd *cobra.Command, args []string) {

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret, Ignore: ignore}

		content, err := rest.SendGet("executionzones/" + strconv.Itoa(id) + "/actions/list")
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
