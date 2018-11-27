def call(stackname, folder, commitStatusName='smoke_tests') {
    def stack = Stack.fromName(stackname)
    def String commit = elifeGitRevision()
    def smokeTestsCmd = "smoke-tests ${folder}"
    def smokeTests = withCommitStatus({
        builderCmd stackname, smokeTestsCmd
    }, "${stack.environment()}/smoke-tests", commit)
}
