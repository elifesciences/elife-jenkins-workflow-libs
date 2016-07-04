def call(stackname, folder, propagateFailure=false) {
    def projectTestsCmd = "cd ${folder}; ./project_tests.sh"
    if (!propagateFailure) {
        projectTestsCmd = "${projectTestsCmd} || echo TESTS_FAILED"
    }
    builderCmd stackname, projectTestsCmd
    def smokeTestsCmd = "cd ${folder}; test -e smoke_tests.sh && ./smoke_tests.sh || echo 'No smoke tests to execute'"
    builderCmd stackname, smokeTestsCmd
}
