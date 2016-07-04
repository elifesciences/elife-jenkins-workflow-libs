def call(stackname, cmd) {
    cmd = cmd.replaceAll('=', '\\=')
    cmd = cmd.replaceAll(/'/, "\\\\'")
    print "About to execute: ${cmd}"
    sh "${env.BUILDER_PATH}bldr 'cmd:${stackname},${cmd}'"
}
