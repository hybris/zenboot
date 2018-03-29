package cmd

import (
	"fmt"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

type CustomersResponse struct {
	Customers []Customer `json:"customers"`
}

type Customer struct {
	Id           int      `json:"id"`
	Email        string   `json:"email"`
	CreationDate string   `json:"creationDate"`
	Hosts        []string `json:"hosts"`
}

func init() {
	listCmd.AddCommand(listcustomersCmd)
}

var listcustomersCmd = &cobra.Command{
	Use:   "customers [flags]",
	Short: "list all customers",
	Run: func(cmd *cobra.Command, args []string) {

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret, Ignore: ignore}

		content, err := rest.SendGet("customers/list")
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
