def call(localTestArtifact, stackname=null, remoteTestArtifact = null) {
    if (stackname) {
        if (remoteTestArtifact == null) {
            throw new Exception("When specifying a `stackname`, you must also specify a `remoteTestArtifact` to retrieve");
        }
        def temporarylocalTestArtifact="/tmp/${localTestArtifact}"
        sh "touch ${temporarylocalTestArtifact}"
        sh "chmod 666 ${temporarylocalTestArtifact}"
        sh "sudo -H -u elife ${env.OLD_BUILDER_SCRIPTS_PREFIX}download_file ${stackname},$remoteTestArtifact,$temporarylocalTestArtifact"
        sh "cp ${temporarylocalTestArtifact} ${localTestArtifact}"
    }
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
