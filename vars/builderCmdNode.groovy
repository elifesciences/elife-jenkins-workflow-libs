def call(String stackname, String node, String cmd, folder=null, captureOutput=false) {
    if (folder) {
        cmd = "cd ${folder} && " + cmd;
    }
    cmd = _escapeCmd(cmd)
    def additionalBuilderOptions = ",node=${node}"
    if (captureOutput) {
        additionalBuilderOptions = additionalBuilderOptions + ",clean_output=1"
    }
    def shellCmd = "${env.BUILDER_PATH}bldr 'cmd:${stackname},${cmd}${additionalBuilderOptions}'"
    if (captureOutput) {
        return sh(script: shellCmd, returnStdout:true)
    } else {
        sh shellCmd
    }
}
