def call(localTestArtifact, stackname=null, remoteTestArtifact = null) {
    if (stackname) {
        if (remoteTestArtifact == null) {
            throw new Exception("When specifying a `stackname`, you must also specify a `remoteTestArtifact` to retrieve");
        }
        sh "touch ${temporarylocalTestArtifact}"
        sh "chmod 666 ${temporarylocalTestArtifact}"
        sh "sudo -H -u elife ${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifact},${localTestArtifact}"
    }
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
