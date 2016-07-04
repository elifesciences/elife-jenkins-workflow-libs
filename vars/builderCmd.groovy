def call(stackname, cmd) {
    cmd = cmd.replaceAll('=', '\\=')
    cmd = cmd.replaceAll(/'/, "\\\\'")
    def shellCmd = "${env.BUILDER_PATH}bldr 'cmd:${stackname},${cmd}'"
    print "About to execute: ${shellCmd}"
    sh shellCmd 
}
