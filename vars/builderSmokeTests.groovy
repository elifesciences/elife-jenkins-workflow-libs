def call(stackname, folder, commitStatusName='smoke_tests') {
    def String commit = elifeGitRevision()
    def smokeTestsCmd = "smoke-tests ${folder}"
    def smokeTests = withCommitStatus({
        builderCmd stackname, smokeTestsCmd
    }, 'smoke_tests', commit)
}
