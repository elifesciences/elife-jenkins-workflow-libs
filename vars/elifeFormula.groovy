def call(String project, String smokeTestsFolder = '', String formula = null) {
    elifePipeline {
        def commit
        stage 'Checkout', {
            checkout scm
            commit = elifeGitRevision()
        }

        elifePullRequestOnly { prNumber ->
            if (formula == null) {
                formula = "${project}-formula"
                // simplify the instance name since there is no ambiguity as to which formula we are testing
                // helps with hostname length limit of 64 bytes on Linux
                // pr-1--lax.elife.internal
                def instance = "pr-${prNumber}"
            } else {
                // builder-base-formula-pr-1--heavybox.elife.internal
                def instance = "${formula}-pr-${prNumber}"
            }
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

                    def variantsTopFilesString = sh script: 'cd salt/; ls -1 example-*.top || true', returnStdout: true
                    def variantsTopFiles = variantsTopFilesString.readLines()
                    for (i = 0; i < variantsTopFiles.size(); i++) {
                        def topFile = variantsTopFiles.get(i)
                        def variant = topFile - "example-" - ".top"

                        stage "Fresh variant ${variant}", {
                            try {
                                elifeGithubCommitStatus commit, 'pending', "continuous-integration/jenkins/pr-fresh-variant-${variant}", "Fresh variant ${variant} stack creation started", env.RUN_DISPLAY_URL
                                sh "/srv/builder/bldr ensure_destroyed:${stackname}"
                                sh "BUILDER_TOPFILE=${topFile} /srv/builder/bldr masterless.launch:${project},${instance},standalone,${formula}@${commit}"
                                if (smokeTestsFolder) {
                                    builderSmokeTests stackname, smokeTestsFolder
                                }
                                elifeGithubCommitStatus commit, 'success', "continuous-integration/jenkins/pr-fresh-variant-${variant}", "Fresh variant ${variant} creation succeeded", env.RUN_DISPLAY_URL
                            } catch (e) {
                                elifeGithubCommitStatus commit, 'failure', "continuous-integration/jenkins/pr-fresh-variant-${variant}", "Fresh variant ${variant} creation failed", env.RUN_DISPLAY_URL
                                throw e
                            }
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
