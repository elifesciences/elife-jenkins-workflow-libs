import Revision

def call(commitOrMap, status=null, context=null, description='elifeGithubCommitStatus step', targetUrl='') {
    if (commitOrMap instanceof Map) {
        commitSha = commitOrMap['commit']
        status = commitOrMap['status']
        context = commitOrMap['context']
        description = commitOrMap.get('description', 'elifeGithubCommitStatus step')
        targetUrl = commitOrMap.get('targetUrl', '')
        repository = commitOrMap.get('repository')
        if (!repository) {
            repository = ''
        }
    } else {
        commitSha = commitOrMap
    }
    def revision = new Revision(commitSha)
    if (revision.isBranch()) {
        echo "No commit status to update for branch ${revision.toString()}"
    } else {
        sh "repository=${repository} commit=${commitSha} status=${status} context=${context} description='${description}' target_url='${targetUrl}' /usr/local/jenkins-scripts/notify_github_commit_status.sh"
    }
}
