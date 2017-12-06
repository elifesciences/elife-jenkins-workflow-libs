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
            lock (stackname) {
                try {
                    stage 'Fresh', {
                        try {
                            elifeGithubCommitStatus commit, 'pending', 'continuous-integration/jenkins/pr-fresh', 'Fresh stack creation started', env.RUN_DISPLAY_URL
                            sh "/srv/builder/bldr ensure_destroyed:${stackname}"
                            sh "/srv/builder/bldr masterless.launch:${project},${instance},standalone,${formula}@${commit}"
                            if (smokeTestsFolder) {
                                builderSmokeTests stackname, smokeTestsFolder
                            }
                            elifeGithubCommitStatus commit, 'success', 'continuous-integration/jenkins/pr-fresh', 'Fresh stack creation succeeded', env.RUN_DISPLAY_URL
                        } catch (e) {
                            elifeGithubCommitStatus commit, 'failure', 'continuous-integration/jenkins/pr-fresh', 'Fresh stack creation failed', env.RUN_DISPLAY_URL
                            throw e
                        }
                    }

                    stage 'Basic stack', {
                        try {
                            elifeGithubCommitStatus commit, 'pending', 'continuous-integration/jenkins/pr-base', 'Original stack creation started', env.RUN_DISPLAY_URL
                            sh "/srv/builder/bldr ensure_destroyed:${stackname}"
                            sh "/srv/builder/bldr masterless.launch:${project},${instance},standalone"
                            if (smokeTestsFolder) {
                                builderSmokeTests stackname, smokeTestsFolder
                            }
                            elifeGithubCommitStatus commit, 'success', 'continuous-integration/jenkins/pr-base', 'Original stack creation succeeded', env.RUN_DISPLAY_URL
                        } catch (e) {
                            elifeGithubCommitStatus commit, 'failure', 'continuous-integration/jenkins/pr-base', 'Original stack creation failed', env.RUN_DISPLAY_URL
                            throw e
                        }
                    }

                    stage 'Applying change', {
                        try {
                            elifeGithubCommitStatus commit, 'pending', 'continuous-integration/jenkins/pr-update', 'Applying update started', env.RUN_DISPLAY_URL
                            sh "/srv/builder/bldr masterless.set_versions:${stackname},${formula}@${commit}"
                            sh "/srv/builder/bldr update:${stackname}"
                            if (smokeTestsFolder) {
                                builderSmokeTests stackname, smokeTestsFolder
                            }
                            elifeGithubCommitStatus commit, 'success', 'continuous-integration/jenkins/pr-update', 'Applying update succeeded', env.RUN_DISPLAY_URL
                        } catch (e) {
                            elifeGithubCommitStatus commit, 'failure', 'continuous-integration/jenkins/pr-update', 'Applying update failed', env.RUN_DISPLAY_URL
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
}
