package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
  "os"
)

var rest_call string

func init() {
  callCmd.Flags().StringVarP(&rest_call, "rest_call", "r", "help", "the endpoint to GET from")
  RootCmd.AddCommand(callCmd)
}

var callCmd = &cobra.Command {
  Use:   "call [endpoint]",
  Short: "Test a rest-command with zenboot",
  Run: func(cmd *cobra.Command, args []string) {
      content, err := sendGet(rest_call)
      if err != nil {
          fmt.Println(err)
          os.Exit(1)
      }
      fmt.Printf("%s\n", content)
  },
}
