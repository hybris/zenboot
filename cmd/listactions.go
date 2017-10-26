package cmd

import (
	"fmt"
	"os"

	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

func init() {
	RootCmd.AddCommand(listactionsCmd)
}

var listactionsCmd = &cobra.Command{
	Use:   "listactions [ExecutionZoneID]",
	Short: "list all action names of the specific execution zone",
	Run: func(cmd *cobra.Command, args []string) {
		if len(args) != 1 {
			fmt.Println("Please specify an id for the Execution Zone.")
			os.Exit(1)
		}

		id := args[0]

		content, err := sendGet("executionzones/" + id + "/listactions")
		if err != nil {
			fmt.Println("Error: ", err)
			os.Exit(1)
		}

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
