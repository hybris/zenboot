package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
  "os"
)

func init() {
  RootCmd.AddCommand(callCmd)
}

var callCmd = &cobra.Command {
  Use:   "call [rest-command]",
  Short: "Test a rest-command with zenboot",
  Run: func(cmd *cobra.Command, args []string) {
      if len(args) < 1 {
          fmt.Println("This command needs at least a rest-command to be passed.")
          os.Exit(1)
      }
      rest_call := args[0]

      content, err := sendGet(rest_call)
      if err != nil {
          fmt.Println(err)
          os.Exit(1)
      }
      fmt.Printf("%s\n", content)
  },
}
