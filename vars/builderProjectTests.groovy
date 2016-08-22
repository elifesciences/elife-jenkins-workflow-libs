def call(stackname, folder, propagateFailure=false, testArtifacts=[]) {
    def projectTestsCmd = "cd ${folder}; ./project_tests.sh"
    if (!propagateFailure) {
        projectTestsCmd = "${projectTestsCmd} || echo TESTS_FAILED"
    }
    try {
        builderCmd stackname, projectTestsCmd
    } catch (e) {
        for (int i = 0; i < testArtifacts.size(); i++) {
            def remoteTestArtifact = testArtifacts.get(i)
            def slash = remoteTestArtifact.lastIndexOf('/')
            def basename = remoteTestArtifact[slash+1..-1]
            def localTestArtifact = "${env.BUILD_TAG}.{$basename}"
            builderTestArtifact localTestArtifact, stackname, remoteTestArtifact
        }
        throw e
    }
    
    builderSmokeTests stackname, folder
}
