package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
)

func init() {
  RootCmd.AddCommand(resttestCmd)
}

var resttestCmd = &cobra.Command {
  Use:   "resttest",
  Short: "Test a rest-command with zenboot-test",
  Run: func(cmd *cobra.Command, args []string) {
      var content = sendRequest("GET", "help")
      fmt.Printf("%s\n", content)
  },
}
