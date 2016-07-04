def call(stackname, cmd) {
    cmd = cmd.replaceAll('=', '\\=')
    cmd = cmd.replaceAll(/'/, "\\\\'")
    sh "${env.BUILDER_PATH}bldr 'cmd:${stackname},${cmd}'"
}
