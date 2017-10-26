package lib

import (
	"fmt"
	"testing"
)

type Client struct {
	Response []byte
}

var test_client Client = Client{Response: []byte("")}

func testSendRequest(t *testing.T) {
	var rest = Zenboot{ZenbootUrl: "none", Username: "none", Secret: "none"}

	//oldGetClient := getClient

	//defer func() { getClient = oldGetClient }()
	//getClient = func() Client {
	//	return test_client
	//}

	response, _ := rest.SendRequest("GET", "help", nil)

	fmt.Println(string(response))

}
