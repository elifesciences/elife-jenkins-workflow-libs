def call(branch, title, description = '', temporaryFile = 'pull-request.log') {
    sh "git push origin ${branch}"
    def fullText = title + "\n\n" + description
    writeFile(file: temporaryFile, text: fullText)
    sh "hub pull-request -F ${temporaryFile} -h ${branch}"
    sh "rm ${temporaryFile}"
}
