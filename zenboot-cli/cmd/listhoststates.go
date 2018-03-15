package cmd

import (
	"fmt"
	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)


func init() {
	listCmd.AddCommand(listhoststatesCmd)
}

var listhoststatesCmd = &cobra.Command{
	Use:   "hoststates",
	Short: "list all host states",
	Run: func(cmd *cobra.Command, args []string) {

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret, Ignore: ignore}

		content, err := rest.SendGet("hoststates")
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
