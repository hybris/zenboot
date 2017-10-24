package cmd

import (
  "github.com/spf13/cobra"
  "fmt"
  "os"
  "github.com/hokaccha/go-prettyjson"
)

var domain string

func init() {
  RootCmd.AddCommand(listzonesCmd)
  listzonesCmd.Flags().StringVarP(&domain, "domain", "d","", "Domain to match zones to")
}

var listzonesCmd = &cobra.Command {
  Use: "listzones -d <ExecutionZone>",
  Short: "list all matching execution zones",
  Run: func(cmd *cobra.Command, args []string){
    if domain == "" {
      fmt.Println("Error!")
      os.Exit(1)
    }

	  content, err := sendGet("executionzones/list")
	  if err != nil {
		  fmt.Println(err)
		  os.Exit(1)
	  }




	  prettyjson, _ := prettyjson.Format(content)
	  fmt.Printf("%s\n", prettyjson)
  },
}

