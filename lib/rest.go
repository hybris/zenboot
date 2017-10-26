package lib

import (
	"fmt"
	"net/http"
	"io/ioutil"
    "net"
    "os"
    "time"
    "strings"
    "bytes"
)

var ENDPOINT = "/zenboot/rest/v1/"

type Zenboot struct {
    ZenbootUrl string
    Username string
    Secret string
}

func HandleError(err error) {
  if err != nil {
    fmt.Println("There was an error: ", err)
    os.Exit(1)
  }
}

func (z Zenboot) SendGet(rest_call string) ([]byte, error) {
    result, err := z.SendRequest("GET", rest_call, nil)
    return result, err
}

func (z Zenboot) SendPost(rest_call string, data []byte) ([]byte, error) {
    result, err := z.SendRequest("POST", rest_call, data)
    return result, err
}

func (z Zenboot) SendRequest(request_type string, rest_call string, data []byte) ([]byte, error) {

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

    data_buffer := bytes.NewBuffer(data)

	req, err := http.NewRequest(request_type, z.ZenbootUrl+ENDPOINT+rest_call, data_buffer)
    HandleError(err)
	req.SetBasicAuth(z.Username, z.Secret)
    req.Header.Set("Accept", "application/json")

    if request_type == "POST" {
        req.Header.Set("Content-Type", "application/json")
    }

	resp, err := client.Do(req)
    if err != nil {
        return nil, err
    }

	defer resp.Body.Close()

	content, err := ioutil.ReadAll(resp.Body)
	if err != nil {
	    return nil, err
	}

    var returnValue = string(content)

    if strings.Contains(returnValue, "Status 401") {
        fmt.Println("Wrong or missing credentials. Please set correct credentials.")
        os.Exit(1)
    } else if strings.Contains(returnValue, "Status 403") {
        fmt.Println("Insufficient permissions to access resource.")
        os.Exit(1)
    }

    return content, nil
}
