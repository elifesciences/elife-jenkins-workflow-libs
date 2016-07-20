def call(localTestArtifact, stackname=null, remoteTestArtifact = null) {
    // https://issues.jenkins-ci.org/browse/JENKINS-33511
    env.WORKSPACE = pwd()

    if (stackname) {
        if (remoteTestArtifact == null) {
            throw new Exception("When specifying a `stackname`, you must also specify a `remoteTestArtifact` to retrieve");
        }

        def absolutePathOfLocalTestArtifact = "${env.WORKSPACE}/$localTestArtifact"
        sh "touch ${absolutePathOfLocalTestArtifact}"
        sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifact},${absolutePathOfLocalTestArtifact}"
    }
    if (!fileExists(localTestArtifact)) {
        error "Tests failed without leaving around an artifact."
    }
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
