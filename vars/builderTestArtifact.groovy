def call(remoteTestArtifact, stackname) {
    // https://issues.jenkins-ci.org/browse/JENKINS-33511
    env.WORKSPACE = pwd()

    def localTestArtifact = ((remoteTestArtifact =~ /\/(build\/.*)/)[0][1])
    def slash = localTestArtifact.lastIndexOf('/')
    def localTestArtifactFolder = localTestArtifact[0..slash]

    // builder runs in its own folder as working directory
    echo "Downloading on ${localTestArtifact}"
    sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifact},${localTestArtifactFolder},allow_missing=True"

    if (!readFile(localTestArtifact)) {
        echo "Artifact ${localTestArtifact} not found"
        return
    }
    echo "Found ${localTestArtifact}"
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
