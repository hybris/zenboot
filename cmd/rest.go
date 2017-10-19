package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
)

func init() {
  RootCmd.AddCommand(restCmd)
}

var restCmd = &cobra.Command {
  Use:   "rest",
  Short: "Use the REST interface directly.",
  Run: func(cmd *cobra.Command, args []string) {
  },
}
