package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
)

var endpoint string

func init() {
  resttestCmd.Flags().StringVarP(&endpoint, "endpoint", "e", "hoststates", "the endpoint to GET from")
  RootCmd.AddCommand(resttestCmd)
}

var resttestCmd = &cobra.Command {
  Use:   "resttest [endpoint]",
  Short: "Test a rest-command with zenboot-test",
  Run: func(cmd *cobra.Command, args []string) {
      var content = sendRequest("GET", endpoint)
      fmt.Printf("%s\n", content)
  },
}
