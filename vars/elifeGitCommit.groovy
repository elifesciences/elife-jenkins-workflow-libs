def call(message, temporaryFile='commit-message.log') {
    writeFile(file: temporaryFile, text:message)
    sh "git commit -F ${temporaryFile}"
    sh "rm -f ${temporaryFile}"
}
