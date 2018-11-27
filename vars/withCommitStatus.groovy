def call(Closure body, String name, String commit, String repository = null) {
    elifeGithubCommitStatus(
        'commit': commit, 
        'repository': repository,
        'status': 'pending',
        'context': "continuous-integration/jenkins/${name}",
        'description': "${name} started", 
        'displayUrl': env.RUN_DISPLAY_URL
    )
    try {
        body()
        elifeGithubCommitStatus commit, 'success', "continuous-integration/jenkins/${name}", "${name} succeeded", env.RUN_DISPLAY_URL
    } catch (e) {
        elifeGithubCommitStatus commit, 'failure', "continuous-integration/jenkins/${name}", "${name} failed: ${e.message}", env.RUN_DISPLAY_URL
        throw e
    }
}
