package cmd

import (
	"fmt"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

func init() {
	listCmd.AddCommand(listserviceurlsCmd)
}

var listserviceurlsCmd = &cobra.Command{
	Use:   "serviceurls [flags]",
	Short: "list all serviceurls",
	Run: func(cmd *cobra.Command, args []string) {

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret}

		content, err := rest.SendGet("serviceurls/list")
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
