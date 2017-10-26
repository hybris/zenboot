package cmd

import (
	"encoding/json"
	"fmt"
	"os"
	"strconv"
	"strings"

	"../lib"
	"github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

var action string

type JsonResponse struct {
	Executions []Execution `json:"executions"`
}

type Execution struct {
	Parameters []Parameter `json:"parameters"`
}

type Parameter struct {
	ParameterName  string `json:"parameterName"`
	ParameterValue string `json:"parameterValue"`
}

func init() {
	executeCmd.Flags().StringSliceP("parameter", "p", nil, "a parameter to pass to the execution")
	RootCmd.AddCommand(executeCmd)
}

var executeCmd = &cobra.Command{
	Use:   "execute [action]",
	Short: "Execute an action in an Execution Zone with zenboot.",
	Run: func(cmd *cobra.Command, args []string) {
		if id == 0 {
			fmt.Println("Please specify an id for the Execution Zone.")
			os.Exit(1)
		} else if len(args) < 1 {
			fmt.Println("Please specify an action to execute.")
			os.Exit(1)
		}
		slicePFlag, _ := cmd.Flags().GetStringSlice("parameter")

		action := args[0]

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret}

		parameters, err := rest.SendGet("executionzones/" + strconv.Itoa(id) + "/actions/" + action + "/listparams")
		lib.HandleError(err)

		jsonParameters := JsonResponse{}
		json.Unmarshal(parameters, &jsonParameters)

		var emptyParams map[string]bool = make(map[string]bool)

		for execId, execution := range jsonParameters.Executions {
			for paramId, params := range execution.Parameters {
				if params.ParameterValue == "" {
					emptyParams[params.ParameterName] = true
					for _, flag := range slicePFlag {
						paramMap := strings.SplitN(flag, "=", 2)
						if params.ParameterName == paramMap[0] {
							jsonParameters.Executions[execId].Parameters[paramId].ParameterValue = paramMap[1]
							delete(emptyParams, params.ParameterName)
						}
					}
				}
			}
		}

		if len(emptyParams) > 0 {
			fmt.Println("\x1b[31mThe action cannot be executed. There are empty parameters:\n\x1b[0m")
			for key, _ := range emptyParams {
				fmt.Println(" - ParameterName [", key, "] has no value")
			}
			fmt.Println("")
			os.Exit(1)
		}

		setParameters, err := json.Marshal(jsonParameters)
		lib.HandleError(err)

		callback, err := rest.SendPost("executionzones/"+strconv.Itoa(id)+"/actions/"+action+"/1/execute", []byte(setParameters))
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(callback)
		fmt.Println(string(prettyjson))
	},
}
