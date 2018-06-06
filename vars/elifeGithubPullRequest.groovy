import EscapeString

def call(branch, title, description = '', base='master', temporaryFile = 'pull-request.log') {
    def issuesList = sh script: "hub issue", returnStdout: true
    echo "Issues list: $issuesList"
    if (issuesList.contains(title)) {
        echo "There is already an existing PR with title: ${title}"
        return
    }
    sh "git push origin ${branch}"
    def fullText = title + "\n\n" + description
    writeFile(file: temporaryFile, text: fullText)
    sh "hub pull-request -F ${temporaryFile} -b ${base} -h ${branch}"
    sh "rm ${temporaryFile}"
}
