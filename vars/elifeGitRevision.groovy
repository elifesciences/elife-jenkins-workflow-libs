def call() {
    def commitFile = "${env.BUILD_TAG}.commit.txt"
    sh "git rev-parse HEAD > ${commitFile}"
    def commitFileContent = readFile commitFile
    def commit = commitFileContent.trim()
    sh "rm ${commitFile}"
    return commit
}
