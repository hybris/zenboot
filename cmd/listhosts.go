package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
  "github.com/hokaccha/go-prettyjson"
  "../lib"
)

type HostsResponse struct {
    Hosts []Host  `json:"hosts"`
}

type Host struct {
    HostName string  `json:"hostname"`
    CName string  `json:"cname"`
    HostState string  `json:"hoststate"`
    IPAdress string  `json:"ipaddress"`
    ServiceUrls []string  `json:"serviceUrls"`
}

func init() {
  RootCmd.AddCommand(listhostsCmd)
}

var listhostsCmd = &cobra.Command {
  Use: "list hosts [flags]",
  Short: "list all CREATED and COMPLETED hosts [matching the given domain]",
  Run: func(cmd *cobra.Command, args []string){

    var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret}

    content, err := rest.SendGet("hosts?hostState=CREATED,COMPLETED")
    lib.HandleError(err)

    prettyjson, _ := prettyjson.Format(content)
    fmt.Println(string(prettyjson))
  },
}

