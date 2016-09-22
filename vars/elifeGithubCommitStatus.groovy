def call(status, description) {
    sh "status=${status} description=${description} /usr/local/jenkins-scripts/notify_github_commit_status.sh"
}
