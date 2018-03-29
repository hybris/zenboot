package cmd

import (
	"fmt"
	"strconv"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

func init() {
	listCmd.AddCommand(listAttribsCmd)
}

var listAttribsCmd = &cobra.Command{
	Use:   "attributes [flags]",
	Short: "list all attributes of an Execution Zone",
	Run: func(cmd *cobra.Command, args []string) {
		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret, Ignore: ignore}

		content, err := rest.SendGet("executionzones/" + strconv.Itoa(id) + "/attributes/list")
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
