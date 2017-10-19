package cmd

import (
	"fmt"
	"net/http"
	"io/ioutil"
    "net"
    "time"
)

var BASE_URL = "https://zenboot.hybris.com/zenboot/rest/v1/"

func sendRequest(request_type string, endpoint string) string {

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
	req, err := http.NewRequest(request_type, BASE_URL+endpoint, nil)
	req.SetBasicAuth("nobody", "nobody")
    req.Header.Set("Accept", "application/json")

	resp, err := client.Do(req)

	defer resp.Body.Close()

	content, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println(err)
	}

    return string(content)
}
