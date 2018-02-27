def call(stackname, cmd, folder=null, captureOutput=false, concurrency=null) {
    if (folder) {
        cmd = "cd ${folder} && " + cmd;
    }
    cmd = _escapeCmd(cmd)
    def additionalBuilderOptions = ""
    if (captureOutput) {
        additionalBuilderOptions = ",clean_output=1"
    }
    if (concurrency) {
        additionalBuilderOptions = ",concurrency=${concurrency}"
    }
    def shellCmd = "${env.BUILDER_PATH}bldr 'cmd:${stackname},${cmd}${additionalBuilderOptions}'"
    if (captureOutput) {
        return sh(script: shellCmd, returnStdout:true)
    } else {
        sh(script: shellCmd)
    }
}
