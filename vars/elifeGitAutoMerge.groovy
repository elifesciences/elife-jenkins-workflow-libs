def call(branch, title, description = '', base='master') {
    def localBase = base
    sh "git checkout ${localBase}"
    sh "git pull origin ${localBase}"
    // TODO: also use description, but it's complex to escape it to pass in on the command line
    // maybe if there's a way to specify a file like in elifeGithubPullRequest
    sh "git merge ${branch} -m ${title}"
    sh "git push origin ${localBase}"
}
