def call(project, commit, testArtifacts=[], organization='elifesciences') {
    def fullImageName = "${organization}/${project}_ci:${commit}"
    def String container = "${project}_ci_project_tests"
    def folder = dockerReadEnv fullImageName, 'PROJECT_FOLDER'
    if (!folder) {
        throw new Exception("The PROJECT_FOLDER environment variable must be configured in the container image, for the projects tests to be correctly located.")
    }
    try {
        sh "docker rm ${container} || true"
        sh "docker run --name ${container} ${fullImageName}"
    } finally { 
        for (int i = 0; i < testArtifacts.size(); i++) {
            def remoteTestArtifact = new RemoteTestArtifact(testArtifacts.get(i))
            sh "docker cp ${container}:${remoteTestArtifact.remoteTestArtifactFolder()}/. ${remoteTestArtifact.localTestArtifactFolder()}"
            step([$class: 'JUnitResultArchiver', testResults: testArtifacts.get(i)])
        }
    }
}
