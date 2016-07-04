def call(stackname, folder, propagateFailure=false) {
    def projectTestsCmd = "cd ${folder}; ./project_tests.sh"
    if (!propagateFailure) {
        projectTestsCmd = "${projectTestsCmd} || echo TESTS_FAILED"
    }
    builderCmd stackname, projectTestsCmd

    def smokeTestsCmd = "cd ${folder}; if [ -e smoke_tests.sh ]; then ./smoke_tests.sh; fi"
    builderCmd stackname, smokeTestsCmd
}
