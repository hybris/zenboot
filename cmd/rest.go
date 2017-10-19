package cmd

import (
	"fmt"
	"net/http"
	"io/ioutil"
    "net"
    "os"
    "time"
)

var ENDPOINT = "/zenboot/rest/v1/"

func sendRequest(request_type string, rest_call string) (string, error) {

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
	req, err := http.NewRequest(request_type, zenbootUrl+ENDPOINT+rest_call, nil)
	req.SetBasicAuth(username, password)
    req.Header.Set("Accept", "application/json")

	resp, err := client.Do(req)
    if err != nil {
        fmt.Println(err)
        os.Exit(1)
    }

	defer resp.Body.Close()

	content, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println(err)
	}

    return string(content), nil
}
