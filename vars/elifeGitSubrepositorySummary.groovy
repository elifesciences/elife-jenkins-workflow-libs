def call(folder) {
    def commit = sh(script: "cd ${folder}; git rev-parse HEAD", returnStdout: true).trim().substring(0, 7)
    def pullRequestReference = ~/ \(#\d+\)/
    def commitMessage = sh(script: "cd ${folder}; git log -1 --pretty='%B' | head -n 1", returnStdout: true).trim() - pullRequestReference
    return "${commit}: ${commitMessage}"
}
