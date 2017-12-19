package cmd

import (
	"fmt"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

func init() {
	listCmd.AddCommand(listexectypesCmd)
}

var listexectypesCmd = &cobra.Command{
	Use:   "exectypes [flags]",
	Short: "list all exectypes",
	Run: func(cmd *cobra.Command, args []string) {

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret}

		content, err := rest.SendGet("exectypes/list")
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
