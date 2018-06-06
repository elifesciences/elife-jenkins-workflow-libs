def call(branch, title, description = '', base='master', temporaryFile = 'pull-request.log') {
    def isThereAnExistingPullRequest = (sh(script: "hub issue | grep \"${title}\"", returnStatus: true) == 0)
    if (isThereAnExistingPullRequest) {
        echo "There is already an existing PR with title: ${title}"
        return
    }
    sh "git push origin ${branch}"
    def fullText = title + "\n\n" + description
    writeFile(file: temporaryFile, text: fullText)
    sh "hub pull-request -F ${temporaryFile} -b ${base} -h ${branch}"
    sh "rm ${temporaryFile}"
}
