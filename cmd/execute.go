package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
  "os"
  "strconv"
  "encoding/json"
  "strings"
  "github.com/hokaccha/go-prettyjson"
)

var action string
var id int

type JsonResponse struct {
  Executions    []Execution  `json:"executions"`
}

type Execution struct {
  Parameters    []Parameter  `json:"parameters"`
}

type Parameter struct {
  ParameterName  string   `json:"parameterName"`
  ParameterValue string   `json:"parameterValue"`
}

func init() {
  executeCmd.Flags().IntVarP(&id, "executionzone", "e", 0, "the id of the Execution Zone in which to execute.")
  executeCmd.Flags().StringSliceP("parameter", "p", nil, "a parameter to pass to the execution")
  RootCmd.AddCommand(executeCmd)
}

var executeCmd = &cobra.Command {
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

      parameters, err := sendGet("executionzones/"+strconv.Itoa(id)+"/actions/"+action+"/listparams")
      handleError(err)

      jsonParameters := JsonResponse{}
      json.Unmarshal(parameters, &jsonParameters)

      for execId, execution := range jsonParameters.Executions {
          for paramId, params := range execution.Parameters {
            if params.ParameterValue == "" {
                for _, flag := range slicePFlag {
                    paramMap := strings.SplitN(flag, "=", 2)
                    if params.ParameterName == paramMap[0] {
                        jsonParameters.Executions[execId].Parameters[paramId].ParameterValue = paramMap[1]
                    }
                }
            }
          }
      }

      setParameters, err := json.Marshal(jsonParameters)
      handleError(err)

      callback, err := sendPost("executionzones/"+strconv.Itoa(id)+"/actions/"+action+"/1/execute", []byte(setParameters))
      handleError(err)

      prettyjson, _ := prettyjson.Format(callback)
      fmt.Println(string(prettyjson))
  },
}
