def call(Closure body, String name, String commit, String repository = null) {
    elifeGithubCommitStatus(
        'commit': commit, 
        'repository': repository,
        'status': 'pending',
        'context': "continuous-integration/jenkins/${name}",
        'description': "${name} started", 
        'targetUrl': env.RUN_DISPLAY_URL
    )
    try {
        body()
        elifeGithubCommitStatus(
            'commit': commit, 
            'repository': repository,
            'status': 'success',
            'context': "continuous-integration/jenkins/${name}",
            'description': "${name} succeeded",
            'targetUrl': env.RUN_DISPLAY_URL
        )
    } catch (e) {
        elifeGithubCommitStatus(
            'commit': commit,
            'repository': repository,
            'status': 'failure',
            'context': "continuous-integration/jenkins/${name}",
            'description': "${name} failed: ${e.message}",
            'targetUrl': env.RUN_DISPLAY_URL
        )
        throw e
    }
}
