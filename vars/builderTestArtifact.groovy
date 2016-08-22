def call(localTestArtifact, stackname=null, remoteTestArtifact = null, allowMissing = false) {
    // https://issues.jenkins-ci.org/browse/JENKINS-33511
    env.WORKSPACE = pwd()

    if (stackname) {
        if (remoteTestArtifact == null) {
            throw new Exception("When specifying a `stackname`, you must also specify a `remoteTestArtifact` to retrieve");
        }

        def absolutePathOfLocalTestArtifact = "${env.WORKSPACE}/$localTestArtifact"
        def allowMissingParameter = allowMissing ? "True" : "False"
        sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifact},${absolutePathOfLocalTestArtifact},allow_missing=${allowMissingParameter}"
    }
    if (!fileExists(localTestArtifact) && !allowMissing) {
        error "Tests failed without leaving around an artifact."
    }
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
