def call(branch, title, description = '', base='master', temporaryFile = 'pull-request.log') {
    sh "git push origin ${branch}"
    def fullText = title + "\n\n" + description
    writeFile(file: temporaryFile, text: fullText)
    sh "hub pull-request -F ${temporaryFile} -b ${base} -h ${branch}"
    sh "rm ${temporaryFile}"
}
