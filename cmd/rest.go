package cmd

import (
	"fmt"
	"net/http"
	"io/ioutil"
    "net"
    "os"
    "time"
    "bytes"
    "strings"
)

var ENDPOINT = "/zenboot/rest/v1/"

func handleError(err error) {
  if err != nil {
    fmt.Println("There was an error: ", err)
    os.Exit(1)
  }
}

func sendGet(rest_call string) (string, error) {
    result, err := sendRequest("GET", rest_call, "")
    return result, err
}

func sendPost(rest_call string, data string) (string, error) {
    result, err := sendRequest("POST", rest_call, data)
    return result, err
}

func sendRequest(request_type string, rest_call string, data string) (string, error) {

    var netTransport = &http.Transport {
        Dial: (&net.Dialer {
            Timeout: 5 * time.Second,
        }).Dial,
        TLSHandshakeTimeout: 5 * time.Second,
    }
	var client = &http.Client{
        Timeout: time.Second * 10,
        Transport: netTransport,
	}

    data_buffer := bytes.NewBuffer([]byte(data))

	req, err := http.NewRequest(request_type, zenbootUrl+ENDPOINT+rest_call, data_buffer)
    handleError(err)
	req.SetBasicAuth(username, secret)
    req.Header.Set("Accept", "application/json")

	resp, err := client.Do(req)
    if err != nil {
        return "", err
    }

	defer resp.Body.Close()

	content, err := ioutil.ReadAll(resp.Body)
	if err != nil {
	    return "", err
	}

    var returnValue = string(content)

    if strings.Contains(returnValue, "Status 401") {
        fmt.Println("Wrong or missing credentials. Please set correct credentials.")
        os.Exit(1)
    } else if strings.Contains(returnValue, "Status 403") {
        fmt.Println("Insufficient permissions to access resource.")
        os.Exit(1)
    }

    return returnValue, nil
}
