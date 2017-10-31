package cmd

import (
	"fmt"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
	"strconv"
)

type HostsResponse struct {
	Hosts []Host `json:"hosts"`
}

type Host struct {
	HostName    string   `json:"hostname"`
	CName       string   `json:"cname"`
	HostState   string   `json:"hoststate"`
	IPAdress    string   `json:"ipaddress"`
	ServiceUrls []string `json:"serviceUrls"`
}


func init() {
	listhostsCmd.Flags().IntVarP(&id, "executionzone", "e", 0, "Zone filter for hosts")
	listCmd.AddCommand(listhostsCmd)
}

var listhostsCmd = &cobra.Command{
	Use:   "hosts [flags]",
	Short: "list all CREATED and COMPLETED hosts [matching the given execution zone]",
	Run: func(cmd *cobra.Command, args []string) {

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret}

		var filter string

		if id != 0 {
			filter = "&execId=" + strconv.Itoa(id)
		}

		content, err := rest.SendGet("hosts?hostState=CREATED,COMPLETED" + filter)
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(content)
		fmt.Println(string(prettyjson))
	},
}
