def call(Closure updateStep, Closure describeStep, branchPrefix='automated_jenkins_update_', library=false, base_branch='develop', autoMerge=false) {
    if (library) {
        wrapper = elifeLibrary
    } else {
        wrapper = elifePipeline
    }
    def publishStep
    if (autoMerge) {
        publishStep = { branch, title, description, base ->
            elifeGitAutoMerge branch, title, description, base
        }
    } else {
        publishStep = { branch, title, description, base ->
            elifeGithubPullRequest branch, title, description, base
        }
    }
    wrapper {
        def commit
        def branch
        stage 'Checkout and branch', {
            checkout scm
            commit = elifeGitRevision()
            branch = elifeGitGenerateBranch branchPrefix
        }

        stage 'Update', {
            updateStep(commit)
        }

        def differences
        def shortDescription
        stage 'Commit', {
            differences = elifeGitDifferences()
            elifeOnlyIf differences, {
                shortDescription = describeStep()
                elifeGitCommit shortDescription
            }
        }

        stage 'Push and pull request', {
            elifeOnlyIf differences, {
                publishStep branch, shortDescription, "I have run ${env.BUILD_URL} which resulted in this pull request.", base_branch
            }
        }
    }
}
