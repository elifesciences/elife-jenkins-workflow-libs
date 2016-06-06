def call(stackname, remoteTestArtifact, localTestArtifact) {
    def temporarylocalTestArtifact="/tmp/${localTestArtifact}"
    sh "touch ${temporarylocalTestArtifact}"
    sh "chmod 666 ${temporarylocalTestArtifact}"
    sh "sudo -H -u elife ${env.BUILDER_SCRIPTS_PREFIX}download_file ${stackname},$remoteTestArtifact,$temporarylocalTestArtifact"
    sh "cp ${temporarylocalTestArtifact} ${localTestArtifact}"
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
