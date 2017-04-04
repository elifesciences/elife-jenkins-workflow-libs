def call(stackname, folder) {
    def smokeTestsCmd = "smoke-tests ${folder}"
    builderCmd stackname, smokeTestsCmd
}
