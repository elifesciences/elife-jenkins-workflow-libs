def call(Integer prNumber, String body) {
    def escapedBody = escapeString(body)
    sh "number=${prNumber} body='${escapedBody}' /usr/local/jenkins-scripts/post_github_pull_request_comment.sh"
}
