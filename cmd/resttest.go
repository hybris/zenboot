package main

import (
	"fmt"
	"net/http"
	"io/ioutil"
    "net"
    "time"
)

var BASE_URL = "https://zenboot.hybris.com/zenboot/rest/v1/"
//var BASE_URL = "https://zenboot.hybris.com/zenboot/rest/v1/"

func main() {

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
	req, err := http.NewRequest("GET", BASE_URL+"help", nil)
	req.SetBasicAuth("nobody", "nobody")
    req.Header.Set("Accept", "application/json")

	resp, err := client.Do(req)

	defer resp.Body.Close()

	content, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Println(err)
	}

	fmt.Printf("%s\n", content)
}
