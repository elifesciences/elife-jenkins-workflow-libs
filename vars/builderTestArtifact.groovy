def call(RemoteTestArtifact remoteTestArtifact, stackname) {
    // https://issues.jenkins-ci.org/browse/JENKINS-33511
    env.WORKSPACE = pwd()

    def localTestArtifactFolder = remoteTestArtifact.localTestArtifactFolder()
    def localTestArtifact = remoteTestArtifact.localTestArtifact()
    // builder runs in its own folder as working directory
    echo "Downloading: ${localTestArtifact}"
    sh "mkdir -p ${localTestArtifactFolder}"
    if (localTestArtifact.contains('*')) {
        def remoteTestArtifactFolder = remoteTestArtifact.remoteTestArtifactFolder()
        def remoteTestArtifactFolderBasename = remoteTestArtifact.remoteTestArtifactFolderBasename()
        echo "Creating archive artifact.tar"
        sh "${env.BUILDER_PATH}bldr cmd:${stackname},'cd ${remoteTestArtifactFolder}/..; rm -rf artifact.tar; tar -cf artifact.tar $remoteTestArtifactFolderBasename'"
        echo "Downloading archive artifact.tar"
        sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifactFolder}/../artifact.tar,${env.WORKSPACE}/build/artifact.tar"
        echo "Extracting artifact.tar"
        sh "rm -rf ${localTestArtifactFolder}; cd build; tar -xvf artifact.tar"
    } else {
        sh "${env.BUILDER_PATH}bldr download_file:${stackname},${remoteTestArtifact.path()},${env.WORKSPACE}/${localTestArtifactFolder},allow_missing=True"
    }

    echo "Found ${localTestArtifact}"
    step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
}
