package cmd

import (
	"fmt"
	"log"

	"io/ioutil"

	"../lib"
	prettyjson "github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

func init() {
	createCmd.Flags().StringVarP(&paramFile, "file", "f", "", "a file in JSON format containing an Execution Zone Template")
	RootCmd.AddCommand(createCmd)
}

var createCmd = &cobra.Command{
	Use:   "create [flags]",
	Short: "create a new execution zone based on the data in the JSON object",
	Run: func(cmd *cobra.Command, args []string) {
		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret}

		if len(paramFile) <= 0 {
			log.Fatalln("Please prvoide an Execution Zone Template.")
		}
		paramByte, err := ioutil.ReadFile(paramFile)
		if err != nil {
			log.Fatalln(err)
		}

		callback, err := rest.SendPost("executionzones/create", paramByte)
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(callback)
		fmt.Println(string(prettyjson))
	},
}
