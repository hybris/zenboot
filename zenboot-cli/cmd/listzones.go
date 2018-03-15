package cmd

import (
	"encoding/json"
	"fmt"
	"strings"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

var domain string
var zoneType string

type ExecutionZonesResponse struct {
	ExecutionZones []ExecutionZone `json:"executionzones"`
}

type ExecutionZone struct {
	ExecId          int    `json:"execId"`
	ExecType        string `json:"execType"`
	ExecDescription string `json:"execDescription"`
}

func init() {
	listzonesCmd.Flags().StringVarP(&domain, "domain", "d", "", "Domain to match zones to")
	listzonesCmd.Flags().StringVarP(&zoneType, "type", "t", "", "Type to match zones to")
	listCmd.AddCommand(listzonesCmd)
}

var listzonesCmd = &cobra.Command{
	Use:   "zones [flags]",
	Short: "list all Execution Zones [matching the given arguments]",
	Run: func(cmd *cobra.Command, args []string) {

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret, Ignore: ignore}

		content, err := rest.SendGet("executionzones/list")
		lib.HandleError(err)

		jsonZones := ExecutionZonesResponse{}
		json.Unmarshal(content, &jsonZones)

		filteredZones := ExecutionZonesResponse{}
		if domain != "" || zoneType != "" {
			for _, executionZone := range jsonZones.ExecutionZones {
				if strings.Contains(executionZone.ExecDescription, domain) && strings.Contains(executionZone.ExecType, zoneType) {
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
