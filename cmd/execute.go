package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
  "os"
  "strconv"
  "encoding/json"
)

var action string
var id int

type JsonResponse struct {
  Executions    []Execution
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

      for _, flag := range slicePFlag {
          fmt.Println(flag)
      }
      fmt.Println(slicePFlag)

      action := args[0]

      parameters, err := sendGet("executionzones/"+strconv.Itoa(id)+"/actions/"+action+"/listparams")
      handleError(err)

      jsonParameters := JsonResponse{}
      json.Unmarshal([]byte(parameters), &jsonParameters)

      //fmt.Println("First parameter: ", string(jsonParameters.Executions[0].Parameters[0].ParameterName))

      //jsonParameters.Executions[0].Parameters[0].ParameterName = "CHANGED USERNAME"

      for _, execution := range jsonParameters.Executions {
          for _, params := range execution.Parameters {
            fmt.Println(params.ParameterName)
          }
      }

      prettyJSON, error := json.MarshalIndent(jsonParameters, "", "  ")
      handleError(error)

      fmt.Println(string(prettyJSON))

      //callback, err := sendPost("executionzones/"+strconv.Itoa(id)+"/actions/"+action+"/1/execute", prettyJSON)
      //handleError(err)

      //fmt.Printf("%s\n", callback)
  },
}
