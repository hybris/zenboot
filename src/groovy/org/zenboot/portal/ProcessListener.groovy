package org.zenboot.portal

interface ProcessListener {

    void onExecute(String command)

    void onFinish(int exitCode)

    void onOutput(String output)

    void onError(String error)
}
