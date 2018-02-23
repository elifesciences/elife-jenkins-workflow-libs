def call(remoteTestArtifact, stackname) {
    // https://issues.jenkins-ci.org/browse/JENKINS-33511
    env.WORKSPACE = pwd()

    def localTestArtifact = ((remoteTestArtifact =~ /\/(build\/.*)/)[0][1])
    def localSlash = localTestArtifact.lastIndexOf('/')
    def localTestArtifactFolder = localTestArtifact[0..localSlash]
    def allowMissing = ",allow_missing=True"
    if (localTestArtifact.contains('*')) {
        allowMissing = ""
    }

    // builder runs in its own folder as working directory
    echo "Downloading on ${localTestArtifact}"
    sh "mkdir -p ${localTestArtifactFolder}"
    if (localTestArtifact.contains('*')) {
        def remoteSlash = remoteTestArtifact.lastIndexOf('/')
        def remoteTestArtifactFolder = remoteTestArtifact[0..remoteSlash]
        def remoteTestArtifactFolderBasename = (remoteTestArtifactFolder =~ /\/build\/(.*)/)[0][1]
        sh "${env.BUILDER_PATH}bldr cmd:${stackname},'cd ${remoteTestArtifactFolder}/..; rm -rf artifact.tar; tar -cf artifact.tar $remoteTestArtifactFolderBasename'"
        sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifactFolder}/../artifact.tar,${env.WORKSPACE}/build/artifact.tar"
        sh "cd build; tar -xvf artifact.tar"
    } else {
        sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifact},${env.WORKSPACE}/${localTestArtifactFolder}${allowMissing}"
    }

    echo "Found ${localTestArtifact}"
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
