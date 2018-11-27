import Revision

def call(commitOrMap, status, context, description='elifeGithubCommitStatus step', targetUrl='') {
    if (commitOrMap instanceof Map) {
        commitSha = commitOrMap['commit']
        status = commitOrMap['status']
        context = commitOrMap['context']
        description = commitOrMap['description']
        targetUrl = commitOrMap['targetUrl']
    } else {
        commitSha = commitOrMap
    }
    def revision = new Revision(commitSha)
    if (revision.isBranch()) {
        echo "No commit status to update for branch ${revision.toString()}"
    } else {
        sh "commit=${commitSha} status=${status} context=${context} description='${description}' target_url='${targetUrl}' /usr/local/jenkins-scripts/notify_github_commit_status.sh"
    }
}
