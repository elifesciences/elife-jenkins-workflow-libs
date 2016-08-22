def call(stackname, folder, testArtifacts=[]) {
    def projectTestsCmd = "cd ${folder}; ./project_tests.sh"
    try {
        builderCmd stackname, projectTestsCmd
    } catch (e) {
        def localTestArtifacts = []
        for (int i = 0; i < testArtifacts.size(); i++) {
            def remoteTestArtifact = testArtifacts.get(i)
            def slash = remoteTestArtifact.lastIndexOf('/')
            def basename = remoteTestArtifact[slash+1..-1]
            def localTestArtifact = "${env.BUILD_TAG}.{$basename}"
            localTestArtifacts << localTestArtifact
            builderTestArtifact localTestArtifact, stackname, remoteTestArtifact, true
        }
        for (int i = 0; i < localTestArtifacts; i++) {
            def localTestArtifact = localTestArtifacts.get(i)
            elifeVerifyJunitXml localTestArtifact
        }
        throw e
    }
    
    builderSmokeTests stackname, folder
}
