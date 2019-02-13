package lib

import (
	"bytes"
	"crypto/tls"
	"errors"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"net/url"
	"strconv"
	"strings"
	"time"
)

var ENDPOINT = "/zenboot/rest/v1/"

type Zenboot struct {
	ZenbootUrl string
	Username   string
	Secret     string
	Ignore     []string
}

func HandleError(err error) {
	if err != nil {
		log.Fatalln("There was an error: ", err)
	}
}

// ValidateAction encodes an action to make it sutiable for a HTTP request.
// It returns an encoded string and an error if the aciton has an invalid format.
func ValidateAction(action string) (string, error) {
	var err error
	if url.QueryEscape(url.PathEscape(action)) != action {
		err = errors.New("the specified action contains characters which have a special meaning in an URL")
	}
	return url.QueryEscape(url.PathEscape(action)), err
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

	var client = getClient(z.Ignore)
	var data_buffer = bytes.NewBuffer(data)

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

	if resp.StatusCode != http.StatusOK {
		return nil, errors.New("Server didn't respond with 200 OK: " + strconv.Itoa(resp.StatusCode))
	}

	content, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return nil, err
	}

	handlePermissionError(content)

	return content, nil
}

var getClient = get_client

func get_client(ignore []string) *http.Client {
	var netTransport = &http.Transport{
		Dial: (&net.Dialer{
			Timeout:   30 * time.Second,
			KeepAlive: 30 * time.Second,
		}).Dial,
		IdleConnTimeout:     90 * time.Second,
		TLSHandshakeTimeout: 10 * time.Second,
	}
	for _, i := range ignore {
		if i == "cert" {
			netTransport.TLSClientConfig = &tls.Config{InsecureSkipVerify: true}
		}
	}

	var client = &http.Client{
		Timeout:   5 * time.Minute,
		Transport: netTransport,
	}
	return client
}

func handlePermissionError(content []byte) {
	var returnValue = string(content)

	if strings.Contains(returnValue, "Status 401") {
		log.Fatalln("Wrong or missing credentials. Please set correct credentials.")
	} else if strings.Contains(returnValue, "Status 403") {
		log.Fatalln("Insufficient permissions to access resource.")
	}
}
