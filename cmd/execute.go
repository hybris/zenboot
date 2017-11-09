package cmd

import (
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"strconv"
	"strings"

	"io/ioutil"

	"../lib"
	prettyjson "github.com/hokaccha/go-prettyjson"
	"github.com/spf13/cobra"
)

var action string
var paramFile string

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
	executeCmd.Flags().StringVarP(&paramFile, "file", "f", "", "a file in JSON format containing the parameters to pass to the execution")
	RootCmd.AddCommand(executeCmd)
}

var executeCmd = &cobra.Command{
	Use:   "execute [action]",
	Short: "Execute an action in an Execution Zone with zenboot.",
	Run: func(cmd *cobra.Command, args []string) {
		if id == 0 {
			log.Fatalln("Please specify an id for the Execution Zone.")
		} else if len(args) < 1 {
			log.Fatalln("Please specify an action to execute.")
		}

		action := args[0]

		var rest = lib.Zenboot{ZenbootUrl: zenbootUrl, Username: username, Secret: secret}

		parameters, err := rest.SendGet("executionzones/" + strconv.Itoa(id) + "/actions/" + action + "/listparams")
		lib.HandleError(err)

		jsonParameters := JsonResponse{}
		json.Unmarshal(parameters, &jsonParameters)

		var emptyParams map[string]bool = make(map[string]bool)

		var sliceParams []string
		if paramFile != "" {
			paramByte, err := ioutil.ReadFile(paramFile)
			lib.HandleError(err)

			var f map[string]interface{}
			if json.Unmarshal(paramByte, &f) != nil {
				log.Fatalln("The content of the specified file at '" + paramFile + "' is not a valid JSON object.")
			}
			for k, v := range f {
				sliceParams = append(sliceParams, string(k)+"="+v.(string))
			}
		}
		if slicePFlags, _ := cmd.Flags().GetStringSlice("parameter"); len(slicePFlags) > 0 {
			sliceParams = append(sliceParams, slicePFlags...)
		}

		for execId, execution := range jsonParameters.Executions {
			for paramId, params := range execution.Parameters {
				if params.ParameterValue == "" {
					emptyParams[params.ParameterName] = true
				}
				for _, flag := range sliceParams {
					paramMap := strings.SplitN(flag, "=", 2)

					var bye string
					if len(paramMap) != 2 {
						bye = "Please assign a value to each parameter!"
					} else if len(paramMap[1]) == 0 {
						bye = "Parameter values must not be empty!"
					} else if strings.HasSuffix(paramMap[0], "_JSON") {
						var js map[string]interface{}
						if json.Unmarshal([]byte(paramMap[1]), &js) != nil {
							bye = "Paramter " + paramMap[0] + " seems to be an JSON object, however its content is not!"
						}
					}
					lib.HandleError(errors.New(bye))

					if params.ParameterName == paramMap[0] {
						jsonParameters.Executions[execId].Parameters[paramId].ParameterValue = paramMap[1]
						delete(emptyParams, params.ParameterName)
					}
				}
			}
		}

		if len(emptyParams) > 0 {
			fmt.Println("\x1b[31mThe action cannot be executed. There are empty parameters:\n\x1b[0m")
			for key, _ := range emptyParams {
				fmt.Println(" - ParameterName [", key, "] has no value")
			}
			log.Fatalln("")
		}

		setParameters, err := json.Marshal(jsonParameters)
		lib.HandleError(err)

		callback, err := rest.SendPost("executionzones/"+strconv.Itoa(id)+"/actions/"+action+"/1/execute", []byte(setParameters))
		lib.HandleError(err)

		prettyjson, _ := prettyjson.Format(callback)
		fmt.Println(string(prettyjson))
	},
}
