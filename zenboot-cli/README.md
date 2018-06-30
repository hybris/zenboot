# Zenboot Command Line Interface

## Building and installing the CLI

The CLI is written in Golang. To build it, install Golang on your computer (e.g. brew install go) and set the GOPATH. After that, get all the dependencies with
```
go get
```
in the zenboot-cli directory and run
```
go build zenboot.go
```
to build the CLI. The binary file called 'zenboot' can be installed in /usr/local/bin on OS X or Linux. On Windows add it to an appropriate folder and set the PATH variable accordingly.

## Configuration

Before using the CLI, make sure there is a `.zenboot.json` file in your home directory with the following format:
```
{
    "zenbooturl": "https://zenboot.hybris.com",
    "username": "i123456",
    "secret": "$ecretp@ssw0rd"
}
```

## Usage

The CLI is invoked with the `zenboot` command.

`zenboot version` returns the build version.

### Ignore certificate errors
If you are having issues with zenboot and certificate validation (like in case you are developing local), you can use `zenboot -i cert`

### list

The list command can be used to get lists of various items from zenboot:

Command|Function|Example
:-----:|:-----:|:-----:
actions|returns a list of all actions possible in the given execution zone|`zenboot list actions -e 104`
hosts|returns a list of all hosts in the given execution zone|`zenboot list hosts -e 104`
hoststates|returns a list of possible states hosts can have in a given zone|`zenboot list hoststate`
params|returns a list of all parameters a scriptstack can accept|`zenboot list params spin_up_chefserver -e 104`
zones|returns a list of all executionzones. Can be filtered by domain name or type|`zenboot list zones -d albino`

### execute

Executes a scriptstack in the given zone
`zenboot execute -e 104 spin_up_jkmaster`

If the execution requires parameters, they can be passed through
`zenboot execute -e 104 -p SHORTNAME=jkslave10671 delete_vm_by_shortname`

Additionally, parameters can be passed through using a json file
`zenboot execute -e 104 -f parameters.json delete_vm_by_shortname`

### admin commands

The Zenboot CLI also contains several commands useful for administering Zenboot

Command|Function|Example
:-----:|:-----:|:-----:
create|create a new execution zone based on the data in a JSON object|`zenboot create -f file.json`
gettemplate|generate a template file for creating a new zone|`zenboot gettemplate -o file.json'`

### raw REST calls

The Zenboot CLI is also capable of performing direct calls to the zenboot REST API with the `call` function
`zenboot call executionzones/104/listactions` will return the same output as `zenboot list actions -e 104`
More information is available here: https://wiki.hybris.com/display/INFRA/Zenboot+REST+Interface
