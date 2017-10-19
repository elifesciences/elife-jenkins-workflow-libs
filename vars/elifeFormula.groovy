def call(String project, String smokeTestsFolder = '', String formula = null) {
    if (formula == null) {
        formula = "${project}-formula"
    }
    elifePipeline {
        def commit
        stage 'Checkout', {
            checkout scm
            commit = elifeGitRevision()
        }

        elifePullRequestOnly { prNumber ->
            def instance = "${formula}-pr-${prNumber}"
            def stackname = "${project}--${instance}"
            try {
                stage 'Basic stack', {
                    try {
                        elifeGithubCommitStatus commit, 'pending', 'continuous-integration/jenkins/pr-base', 'Original stack creation started', env.BUILD_URL
                        sh "/srv/builder/bldr ensure_destroyed:${stackname}"
                        sh "/srv/builder/bldr masterless.launch:${project},${instance}"
                        if (smokeTestsFolder) {
                            builderSmokeTests stackname, smokeTestsFolder
                        }
                        elifeGithubCommitStatus commit, 'success', 'continuous-integration/jenkins/pr-base', 'Original stack creation succeeded', env.BUILD_URL
                    } catch (e) {
                        elifeGithubCommitStatus commit, 'failure', 'continuous-integration/jenkins/pr-base', 'Original stack creation failed', env.BUILD_URL
                        throw e
                    }
                }

                stage 'Applying change', {
                    try {
                        elifeGithubCommitStatus commit, 'pending', 'continuous-integration/jenkins/pr-update', 'Applying update started', env.BUILD_URL
                        sh "/srv/builder/bldr masterless.set_versions:${stackname},${formula}@${commit}"
                        sh "/srv/builder/bldr update:${stackname}"
                        if (smokeTestsFolder) {
                            builderSmokeTests stackname, smokeTestsFolder
                        }
                        elifeGithubCommitStatus commit, 'success', 'continuous-integration/jenkins/pr-update', 'Applying update succeeded', env.BUILD_URL
                    } catch (e) {
                        elifeGithubCommitStatus commit, 'failure', 'continuous-integration/jenkins/pr-update', 'Applying update failed', env.BUILD_URL
                        throw e
                    }
                }
            } finally {
                stage 'Cleanup', {
                    sh "/srv/builder/bldr ensure_destroyed:${stackname}"
                }
            }
        }
    }
}
