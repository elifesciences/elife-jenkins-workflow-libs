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
            def instance = "pr-${prNumber}"
            def stackname = "${project}--${instance}"
            try {
                stage 'Basic stack', {
                    sh "/srv/builder/bldr ensure_destroyed:${stackname}"
                    sh "/srv/builder/bldr masterless.launch:${project},${instance}"
                }

                stage 'Applying change', {
                    sh "/srv/builder/bldr masterless.set_versions:${stackname},${formula}@${commit}"
                    sh "/srv/builder/bldr update:${stackname}"
                }

                if (smokeTestsFolder) {
                    stage 'Smoke tests', {
                        builderSmokeTests stackname, smokeTestsFolder
                    }
                }

                elifeGithubCommitStatus commit, 'success', 'continuous-integration/jenkins/pr-update', 'Applied update successfully', url
            } catch (e) {
                elifeGithubCommitStatus commit, 'failure', 'continuous-integration/jenkins/pr-update', 'Applying update failed', url
                throw e
            } finally {
                stage 'Cleanup', {
                    sh "/srv/builder/bldr ensure_destroyed:${stackname}"
                }
            }
        }
    }
}
