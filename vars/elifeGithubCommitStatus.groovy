def call(commit, status, description='elifeGithubCommitStatus step') {
    sh "commit=${commit} status=${status} description=${description} /usr/local/jenkins-scripts/notify_github_commit_status.sh"
}
