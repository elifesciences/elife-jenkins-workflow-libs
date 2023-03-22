def call(String stackname, Integer node, String cmd, folder=null, captureOutput=false) {
    if (folder) {
        cmd = "cd ${folder} && " + cmd;
    }
    cmd = _escapeCmd(cmd)
    // lsh@2023-03-22: concurrency set to 'serial' as default is 'parallel'.
    // stacktraces for failed commands when run in parallel are longer as the parallel executor
    // prints a stacktrace for any jobs that fail, before failing itself.
    // since this command specifically targets individual nodes there is no need for parallelism.
    def additionalBuilderOptions = ",concurrency=serial,node=${node}"
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
