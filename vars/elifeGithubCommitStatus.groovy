def call(commit, status, context, description='elifeGithubCommitStatus step') {
    sh "commit=${commit} status=${status} context=${context} description='${description}' /usr/local/jenkins-scripts/notify_github_commit_status.sh"
}
