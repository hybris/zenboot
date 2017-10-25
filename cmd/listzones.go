package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
  "os"
  "encoding/json"
  "github.com/hokaccha/go-prettyjson"
  "strings"
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
  listzonesCmd.Flags().StringVarP(&domain, "domain", "d","", "Domain to match zones to")
  RootCmd.AddCommand(listzonesCmd)
}

var listzonesCmd = &cobra.Command {
  Use: "list zones [flags]",
  Short: "list all Execution Zones [matching the given domain]",
  Run: func(cmd *cobra.Command, args []string){

    content, err := sendGet("executionzones/list")
    if err != nil {
      fmt.Println("Error: ", err)
	  os.Exit(1)
    }

    jsonZones := ExecutionZonesResponse{}
    json.Unmarshal(content, &jsonZones)

    filteredZones := ExecutionZonesResponse{}
    if domain != "" {
        for _, executionZone := range jsonZones.ExecutionZones {
            if strings.Contains(executionZone.ExecDescription, domain) {
                filteredZones.ExecutionZones = append(filteredZones.ExecutionZones, executionZone)
            }
        }
    } else {
        filteredZones = jsonZones
    }

    zones, err := json.Marshal(filteredZones)

    prettyjson, _ := prettyjson.Format(zones)
    fmt.Println(string(prettyjson))
  },
}

