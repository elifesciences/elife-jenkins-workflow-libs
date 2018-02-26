def call(Closure body, String name, String commit) {
    elifeGithubCommitStatus commit, 'pending', "continuous-integration/jenkins/${name}", "${name} started", env.RUN_DISPLAY_URL
    try {
        body()
        elifeGithubCommitStatus commit, 'success', "continuous-integration/jenkins/${name}", "${name} succeeded", env.RUN_DISPLAY_URL
    } catch (e) {
        elifeGithubCommitStatus commit, 'failure', "continuous-integration/jenkins/${name}", "${name} failed: ${e.message}", env.RUN_DISPLAY_URL
        throw e
    }
}
