def call(stackname, folder) {
    def String commit = elifeGitRevision()
    def smokeTestsCmd = "smoke-tests ${folder}"
    def smokeTests = withCommitStatus({
        builderCmd stackname, smokeTestsCmd
    }, 'smoke_tests.sh', commit)
}
