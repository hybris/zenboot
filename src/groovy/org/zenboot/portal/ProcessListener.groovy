package org.zenboot.portal

interface ProcessListener {

    def void onExecute(String command)

    def void onFinish(int exitCode)

    def void onOutput(String output)

    def void onError(String error)
}
