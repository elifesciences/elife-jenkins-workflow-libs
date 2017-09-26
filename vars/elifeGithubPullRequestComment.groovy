def call(Integer prNumber, String body, String oncePerPullRequest = null) {
    def escapedBody = _escapeString(body)
    if (oncePerPullRequest) {
        def matchingLines = sh(script: "number=${prNumber} body='${oncePerPullRequest}' bash -x /usr/local/jenkins-scripts/check_github_pull_request_comment.sh", returnStdout: true)
        if (matchingLines > 0) {
            echo "Comment already posted on PR ${prNumber}"
            return false
        }
    }

    sh "number=${prNumber} body='${escapedBody}' bash -x /usr/local/jenkins-scripts/post_github_pull_request_comment.sh"
    return true
}
