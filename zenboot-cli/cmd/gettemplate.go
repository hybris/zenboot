package cmd

import (
	"fmt"
	"../lib"
	"github.com/spf13/cobra"
	"os"
	"bufio"
)

var path string

func init() {
	RootCmd.AddCommand(gettemplateCmd)
	gettemplateCmd.Flags().StringVarP(&path, "output", "o", "", "Zone filter for hosts")
}

var gettemplateCmd = &cobra.Command{
	Use:   "gettemplate -o [PATH]",
	Short: "generate a template file for creating new zones",
	Run: func(cmd *cobra.Command, args []string) {

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret, Ignore: ignore}

		if path == "" {
			path = "./template.json"
		}

		f, err := os.Create(path)
		lib.HandleError(err)
		defer f.Close()

		w := bufio.NewWriter(f)

		content, err := rest.SendGet("executionzones/execzonetemplate")
		lib.HandleError(err)

		fmt.Fprintf(w, string(content))
		w.Flush()
	},
}
