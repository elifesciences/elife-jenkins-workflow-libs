def call(commit, status, context, description='elifeGithubCommitStatus step', targetUrl='') {
    sh "commit=${commit} status=${status} context=${context} description='${description}' targetUrl='${targetUrl}' /usr/local/jenkins-scripts/notify_github_commit_status.sh"
}
