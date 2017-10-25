package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
  "os"
  "encoding/json"
  "github.com/hokaccha/go-prettyjson"
)

var domain string

type ExecutionZonesResponse struct {
    ExecutionZones []ExecutionZone  `json:"executionzones"`
}

type ExecutionZone struct {
    ExecId int  `json:"execId"`
    ExecType string  `json:"execType"`
    ExecDescription string  `json:"execDescription"`
}

func init() {
  RootCmd.AddCommand(listzonesCmd)
  listzonesCmd.Flags().StringVarP(&domain, "domain", "d","", "Domain to match zones to")
}

var listzonesCmd = &cobra.Command {
  Use: "list zones [-d <ExecutionZone>]",
  Short: "list all Execution Zones [matching the given domain]",
  Run: func(cmd *cobra.Command, args []string){

    content, err := sendGet("executionzones/list")
    if err != nil {
      fmt.Println("Error: ", err)
	  os.Exit(1)
    }

    jsonZones := ExecutionZonesResponse{}
    json.Unmarshal(content, &jsonZones)

    if domain != "" {

    }

    zones, err := json.Marshal(jsonZones)

    prettyjson, _ := prettyjson.Format(zones)
    fmt.Println(string(prettyjson))
  },
}

