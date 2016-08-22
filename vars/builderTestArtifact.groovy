def call(localTestArtifact, stackname=null, remoteTestArtifact = null, allowMissing = false) {
    // https://issues.jenkins-ci.org/browse/JENKINS-33511
    env.WORKSPACE = pwd()

    if (stackname) {
        if (remoteTestArtifact == null) {
            throw new Exception("When specifying a `stackname`, you must also specify a `remoteTestArtifact` to retrieve");
        }

        def allowMissingParameter = allowMissing ? "True" : "False"
        /*
        def localTestArtifactFullPath = "${env.WORKSPACE}/${localTestArtifact}"
        echo "Creating empty ${localTestArtifactFullPath}"
        sh "touch ${localTestArtifactFullPath}"
        */
        echo "Creating empty ${localTestArtifact}"
        sh "touch ${localTestArtifact}"
        echo "Downloading onto ${localTestArtifact}"
        sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifact},${localTestArtifact},allow_missing=${allowMissingParameter}"
    }
    if (!readFile(localTestArtifact)) {
        if (allowMissing) {
            echo "Artifact ${localTestArtifact} not found, but was configured to ignore missing artifacts."
            return
        } else {
            error "Tests failed without leaving around an artifact at ${remoteTestArtifact}, which should have been downloaded at ${localTestArtifact}."
        }
    }
    echo "Found ${localTestArtifact}"
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
