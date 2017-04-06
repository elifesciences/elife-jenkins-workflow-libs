def backslash(character, string) {
    // backslash the slash because it's a special character
    // backslash them again because replaceAll has special chars like \1
    return string.replaceAll(character, '\\\\' + character)
}

def backslashQuotes(string) {
    def closedQuote = "'"
    def quote = "\\\\'"
    def openQuote = "'"
    return string.replaceAll(/'/, closedQuote + quote + openQuote)
}

def call(stackname, cmd, folder=null, captureOutput=false) {
    if (folder) {
        cmd = "cd ${folder} && " + cmd;
    }
    cmd = backslash('=', cmd)
    cmd = backslash(',', cmd)
    cmd = backslashQuotes(cmd)
    def additionalBuilderOptions = ""
    if (captureOutput) {
        additionalBuilderOptions = ",clean_output=1"
    }
    def shellCmd = "${env.BUILDER_PATH}bldr 'cmd:${stackname},${cmd}${additionalBuilderOptions}'"
    print "About to execute: ${shellCmd}"
    if (captureOutput) {
        return sh(script: shellCmd, returnStdout:true)
    } else {
        try {
            sh shellCmd
        } catch (e) {
            print "Caused an error: ${shellCmd}"
            print "Message: ${e.message}"
            e.printStackTrace()
            throw e
        }
    }
}
