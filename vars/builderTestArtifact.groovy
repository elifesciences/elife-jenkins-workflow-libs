def call(remoteTestArtifact, stackname) {
    // https://issues.jenkins-ci.org/browse/JENKINS-33511
    env.WORKSPACE = pwd()

    def remoteTestArtifactObject = new RemoteTestArtifact(remoteTestArtifact)
    def localTestArtifactFolder = remoteTestArtifactObject.localTestArtifactFolder()
    def localTestArtifact = ((remoteTestArtifact =~ /\/?(build\/.*)/)[0][1])
    def allowMissing = ",allow_missing=True"
    if (localTestArtifact.contains('*')) {
        allowMissing = ""
    }

    // builder runs in its own folder as working directory
    echo "Downloading: ${localTestArtifact}"
    sh "mkdir -p ${localTestArtifactFolder}"
    if (localTestArtifact.contains('*')) {
        def remoteSlash = remoteTestArtifact.lastIndexOf('/')
        def remoteTestArtifactFolder = remoteTestArtifact[0..remoteSlash]
        def remoteTestArtifactFolderBasename = (remoteTestArtifactFolder =~ /\/build\/(.*)/)[0][1]
        echo "Creating archive artifact.tar"
        sh "${env.BUILDER_PATH}bldr cmd:${stackname},'cd ${remoteTestArtifactFolder}/..; rm -rf artifact.tar; tar -cf artifact.tar $remoteTestArtifactFolderBasename'"
        echo "Downloading archive artifact.tar"
        sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifactFolder}/../artifact.tar,${env.WORKSPACE}/build/artifact.tar"
        echo "Extracting artifact.tar"
        sh "rm -rf ${localTestArtifactFolder}; cd build; tar -xvf artifact.tar"
    } else {
        sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifact},${env.WORKSPACE}/${localTestArtifactFolder}${allowMissing}"
    }

    echo "Found ${localTestArtifact}"
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
