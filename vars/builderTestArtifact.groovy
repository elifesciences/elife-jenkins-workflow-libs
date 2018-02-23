def call(localTestArtifact, stackname, remoteTestArtifact) {
    // https://issues.jenkins-ci.org/browse/JENKINS-33511
    env.WORKSPACE = pwd()

    // builder runs in its own folder as working directory
    def localTestArtifactFolderFullPath = "${env.WORKSPACE}/build/"
    echo "Downloading in ${localTestArtifactFolderFullPath}"
    sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifact},${localTestArtifactFolderFullPath},allow_missing=True"

    if (!readFile(localTestArtifact)) {
        echo "Artifact ${localTestArtifact} not found"
        return
    }
    echo "Found ${localTestArtifact}"
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
