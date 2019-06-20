def call(String project, String smokeTestsFolder = '', String formula = null, List alternatives = []) {
    elifePipeline {
        def commit
        stage 'Checkout', {
            checkout scm
            commit = elifeGitRevision()
            sh "/usr/local/bin/vault-login.sh"
        }

        elifePullRequestOnly { prNumber ->
            def instance
            if (formula == null) {
                formula = "${project}-formula"
                // simplify the instance name since there is no ambiguity as to which formula we are testing
                // helps with hostname length limit of 64 bytes on Linux
                // pr-1--lax.elife.internal
                instance = "pr-${prNumber}"
            } else {
                // builder-base-formula-pr-1--heavybox.elife.internal
                instance = "${formula}-pr-${prNumber}"
            }
            def partialStackname = "${project}--${instance}"
            lock (partialStackname) {
                actions = [:] 
                actions['fresh'] = {
                    try {
                        elifeGithubCommitStatus commit, 'pending', 'continuous-integration/jenkins/pr-fresh', 'Fresh stack creation started', env.RUN_DISPLAY_URL
                        sh "/srv/builder/bldr ensure_destroyed:${partialStackname}-fresh"
                        sh "/srv/builder/bldr masterless.launch:${project},${instance}-fresh,standalone,${formula}@${commit}"
                        if (smokeTestsFolder) {
                            builderSmokeTests "${partialStackname}-fresh", smokeTestsFolder, 'pr-fresh/smoke_tests'
                        }
                        elifeGithubCommitStatus commit, 'success', 'continuous-integration/jenkins/pr-fresh', 'Fresh stack creation succeeded', env.RUN_DISPLAY_URL
                    } catch (e) {
                        elifeGithubCommitStatus commit, 'failure', 'continuous-integration/jenkins/pr-fresh', 'Fresh stack creation failed', env.RUN_DISPLAY_URL
                        throw e
                    } finally {
                        sh "/srv/builder/bldr ensure_destroyed:${partialStackname}-fresh"
                    }
                }

                actions['base-update'] = {
                    try {
                        try {
                            elifeGithubCommitStatus commit, 'pending', 'continuous-integration/jenkins/pr-base', 'Original stack creation started', env.RUN_DISPLAY_URL
                            sh "/srv/builder/bldr ensure_destroyed:${partialStackname}-base-update"
                            sh "/srv/builder/bldr masterless.launch:${project},${instance}-base-update,standalone"
                            if (smokeTestsFolder) {
                                builderSmokeTests "${partialStackname}-base-update", smokeTestsFolder, 'pr-base/smoke_tests'
                            }
                            elifeGithubCommitStatus commit, 'success', 'continuous-integration/jenkins/pr-base', 'Original stack creation succeeded', env.RUN_DISPLAY_URL
                        } catch (e) {
                            elifeGithubCommitStatus commit, 'failure', 'continuous-integration/jenkins/pr-base', 'Original stack creation failed', env.RUN_DISPLAY_URL
                            throw e
                        }

                        try {
                            elifeGithubCommitStatus commit, 'pending', 'continuous-integration/jenkins/pr-update', 'Applying update started', env.RUN_DISPLAY_URL
                            sh "/srv/builder/bldr masterless.set_versions:${partialStackname}-base-update,${formula}@${commit}"
                            sh "/srv/builder/bldr update:${partialStackname}-base-update"
                            if (smokeTestsFolder) {
                                builderSmokeTests "${partialStackname}-base-update", smokeTestsFolder, 'pr-update/smoke_tests'
                            }
                            elifeGithubCommitStatus commit, 'success', 'continuous-integration/jenkins/pr-update', 'Applying update succeeded', env.RUN_DISPLAY_URL
                        } catch (e) {
                            elifeGithubCommitStatus commit, 'failure', 'continuous-integration/jenkins/pr-update', 'Applying update failed', env.RUN_DISPLAY_URL
                            throw e
                        }
                    } finally {
                        sh "/srv/builder/bldr ensure_destroyed:${partialStackname}-base-update"
                    }
                }

                def variantsTopFilesString = sh script: 'cd salt/; ls -1 example-*.top || true', returnStdout: true
                def variantsTopFiles = variantsTopFilesString.readLines()
                for (i = 0; i < variantsTopFiles.size(); i++) {
                    def topFile = variantsTopFiles.get(i)
                    def variant = topFile - "example-" - ".top"

                    actions["fresh variant ${variant}"] = {
                        try {
                            elifeGithubCommitStatus commit, 'pending', "continuous-integration/jenkins/pr-fresh-variant-${variant}", "Fresh variant ${variant} stack creation started", env.RUN_DISPLAY_URL
                            sh "/srv/builder/bldr ensure_destroyed:${partialStackname}-fresh-${variant}"
                            sh "BUILDER_TOPFILE=${topFile} /srv/builder/bldr masterless.launch:${project},${instance}-fresh-${variant},standalone,${formula}@${commit}"
                            if (smokeTestsFolder) {
                                builderSmokeTests "${partialStackname}-fresh-${variant}", smokeTestsFolder
                            }
                            elifeGithubCommitStatus commit, 'success', "continuous-integration/jenkins/pr-fresh-variant-${variant}", "Fresh variant ${variant} creation succeeded", env.RUN_DISPLAY_URL
                        } catch (e) {
                            elifeGithubCommitStatus commit, 'failure', "continuous-integration/jenkins/pr-fresh-variant-${variant}", "Fresh variant ${variant} creation failed", env.RUN_DISPLAY_URL
                            throw e
                        } finally {
                            sh "/srv/builder/bldr ensure_destroyed:${partialStackname}-fresh-${variant}"
                        }
                    }
                }

                for (i = 0; i < alternatives.size(); i++) {
                    def alternative = alternatives.get(i)

                    actions["fresh alternative ${alternative}"] = {
                        try {
                            withCommitStatus({
                                sh "/srv/builder/bldr ensure_destroyed:${partialStackname}-fresh-${alternative}"
                                sh "/srv/builder/bldr masterless.launch:${project},${instance}-fresh-${alternative},${alternative},${formula}@${commit}"
                                if (smokeTestsFolder) {
                                    builderSmokeTests "${partialStackname}-fresh-${alternative}", smokeTestsFolder
                                }
                            }, "pr-fresh-alternative-${alternative}", commit)
                        } finally {
                            sh "/srv/builder/bldr ensure_destroyed:${partialStackname}-fresh-${alternative}"
                        }
                    }
                }

                if (fileExists('helm')) {
                    actions["helm/lint"] = {
                        withCommitStatus({
                            sh "cd helm/ && helm lint *"
                        }, "helm/lint", commit)
                    }
                }

                stage "Provisionings", {
                    parallel actions
                }
            }
        }

        elifeTagOnly { tag ->
            if (fileExists('helm')) {
                stage 'Publish helm chart', {
                    sh 'cd helm && rm -f *.tgz'
                    sh 'cd helm/*/ && helm dependency update .'
                    sh 'cd helm && helm package $(ls -d */)'
                    sh 'cd helm && for p in $(ls *.tgz); do helm s3 push $p alfred; done'
                }
            }
        }
    }
}
