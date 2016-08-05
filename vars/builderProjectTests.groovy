def call(stackname, folder, propagateFailure=false) {
    def projectTestsCmd = "cd ${folder}; ./project_tests.sh"
    if (!propagateFailure) {
        projectTestsCmd = "${projectTestsCmd} || echo TESTS_FAILED"
    }
    builderCmd stackname, projectTestsCmd
    
    builderSmokeTests stackname, folder
}
