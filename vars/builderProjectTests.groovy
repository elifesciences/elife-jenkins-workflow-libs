def retrieveArtifacts(stackname, testArtifacts) {
    def localTestArtifacts = []
    for (int i = 0; i < testArtifacts.size(); i++) {
        def remoteTestArtifact = testArtifacts.get(i)
        def slash = remoteTestArtifact.lastIndexOf('/')
        def basename = remoteTestArtifact[slash+1..-1]
        def localTestArtifact = "${env.BUILD_TAG}.${basename}"
        localTestArtifacts << localTestArtifact
        builderTestArtifact localTestArtifact, stackname, remoteTestArtifact, true
    }
    echo "Retrieved test artifacts: ${localTestArtifacts}"
    for (int i = 0; i < localTestArtifacts.size(); i++) {
        def localTestArtifact = localTestArtifacts.get(i)
        if (fileExists(localTestArtifact)) {
            elifeVerifyJunitXml localTestArtifact
        }
    }
}

def call(stackname, folder, testArtifacts=[]) {
    def projectTestsCmd = "cd ${folder}; ./project_tests.sh"
    try {
        builderCmd stackname, projectTestsCmd
    } finally {
        retrieveArtifacts stackname, testArtifacts
    }
    
    builderSmokeTests stackname, folder
}
