package cmd

import (
	"github.com/spf13/cobra"
)

func init() {
	RootCmd.AddCommand(listCmd)
}

var listCmd = &cobra.Command{
	Use:   "list [flags]",
	Short: "list all things that can be listed",
	Run: func(cmd *cobra.Command, args []string) {
	},
}
