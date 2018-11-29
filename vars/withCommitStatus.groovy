def call(Closure body, nameOrMap, String commit = null, String repository = null) {
    def String name
    def String targetUrl
    if (name instanceof Map) {
        name = nameOrMap['name']
        commit = nameOrMap['commit']
        repository = nameOrMap.get('repository', null)
        targetUrl = nameOrMap.get('targetUrl', env.RUN_DISPLAY_URL)
    } else {
        name = nameOrMap
    }
    elifeGithubCommitStatus(
        'commit': commit, 
        'repository': repository,
        'status': 'pending',
        'context': "continuous-integration/jenkins/${name}",
        'description': "${name} started", 
        'targetUrl': targetUrl
    )
    try {
        body()
        elifeGithubCommitStatus(
            'commit': commit, 
            'repository': repository,
            'status': 'success',
            'context': "continuous-integration/jenkins/${name}",
            'description': "${name} succeeded",
            'targetUrl': targetUrl
        )
    } catch (e) {
        elifeGithubCommitStatus(
            'commit': commit,
            'repository': repository,
            'status': 'failure',
            'context': "continuous-integration/jenkins/${name}",
            'description': "${name} failed: ${e.message}",
            'targetUrl': targetUrl
        )
        throw e
    }
}
