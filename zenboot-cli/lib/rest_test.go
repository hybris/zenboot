package lib

import (
	"encoding/json"
	"io/ioutil"
	"math/rand"
	"net/http"
	"net/http/httptest"
	"testing"
)

func init() {
	rand.Seed(123456789)
}

var letterRunes = []rune("_abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890")
var specialRunes = []rune("°!\"§$%&/()=?`´˚˙¯·˜\\^ﬁ£#”¬“”")
var allRunes = append(specialRunes, letterRunes...)

func randStringRunes(n int) string {
	b := make([]rune, n)
	for i := range b {
		b[i] = letterRunes[rand.Intn(len(letterRunes))]
	}
	return string(b)
}

func randSpecialRunes(n int) string {
	b := make([]rune, n)
	c := false

	for i := 0; i < n; i++ {
		b[i] = allRunes[rand.Intn(len(letterRunes))]
		if c == false {
			for j := range specialRunes {
				if specialRunes[j] == b[i] {
					c = true
				}
			}
		}

		if c == false && i == n-1 {
			i -= (rand.Intn(n) + 1)
		}
	}

	return string(b)
}

func TestValidateAction(t *testing.T) {
	var iter = 1000

	for n := 0; n <= iter; n++ {
		i := randStringRunes(rand.Intn(100) + 1)
		o, e := ValidateAction(i)
		if o != i || e != nil {
			t.Error("Function failed on valid input: '" + i + "'")
		}
	}

	for n := 0; n <= iter; n++ {
		i := randSpecialRunes(rand.Intn(100) + 1)
		o, e := ValidateAction(i)
		if o == i || e == nil {
			t.Error("Function succeeded on invalid input: '" + i + "'")
		}
	}
}

func TestSendRequestWrongResponseStatus(t *testing.T) {
	ts := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusServiceUnavailable)
	}))
	defer ts.Close()

	var rest = Zenboot{ZenbootUrl: ts.URL, Username: "none", Secret: "none"}

	_, err := rest.SendRequest("GET", "help", nil)
	if err == nil {
		t.Error("Function succeeded despite the server being unavailable.")
	}
}

func TestSendGet(t *testing.T) {
	ts := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		if r.Method != "GET" {
			t.Errorf("Expected 'POST' request, got ‘%s’", r.Method)
		}

		if r.URL.EscapedPath() != ENDPOINT+"help" {
			t.Errorf("Expected request to '"+ENDPOINT+"help', got '%s'", r.URL.EscapedPath())
		}
	}))
	defer ts.Close()

	var rest = Zenboot{ZenbootUrl: ts.URL, Username: "none", Secret: "none"}

	_, err := rest.SendGet("help")
	if err != nil {
		t.Error("Function failed despite the server providing an OK response code.")
	}
}

type inputJSON struct {
	Parameters []parameter `json:"executionZoneProperties"`
}

type parameter struct {
	PropertyName  string `json:"propertyName"`
	PropertyValue string `json:"propertyValue"`
}

func TestSendPost(t *testing.T) {
	ts := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		w.WriteHeader(http.StatusOK)
		if r.Method != "POST" {
			t.Errorf("Expected 'POST' request, got ‘%s’", r.Method)
		}

		if r.URL.EscapedPath() != ENDPOINT+"executionzones/create" {
			t.Errorf("Expected request to '"+ENDPOINT+"help', got '%s'", r.URL.EscapedPath())
		}

		if r.Body == nil {
			t.Errorf("Server didn't recieve any input want JSON")
		}
		var bodyBytes []byte
		bodyBytes, _ = ioutil.ReadAll(r.Body)

		jsonParameters := inputJSON{}
		if json.Unmarshal(bodyBytes, &jsonParameters) != nil {
			t.Errorf("Server didn't recieve valid input want JSON")
		}
	}))
	defer ts.Close()

	var rest = Zenboot{ZenbootUrl: ts.URL, Username: "none", Secret: "none"}

	_, err := rest.SendPost("executionzones/create", []byte(`{ "executionZoneProperties": [ { "propertyName": "defaultLifetime", "propertyValue": "" } ] }`))
	if err != nil {
		t.Errorf("Function returned an error: %s", err)
	}
}
