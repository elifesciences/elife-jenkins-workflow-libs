def call(stackname, cmd) {
    cmd = cmd.replaceAll('=', '\\=')
    def closedQuote = "'"
    def quote = "\\\\'"
    def openQuote = "'"
    cmd = cmd.replaceAll(/'/, closedQuote + quote + openQuote)
    def shellCmd = "${env.BUILDER_PATH}bldr 'cmd:${stackname},${cmd}'"
    print "About to execute: ${shellCmd}"
    sh shellCmd 
}
