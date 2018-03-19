def call(project, commit, testArtifacts=[], organization='elifesciences') {
    withCommitStatus({
        def fullImageName = "${organization}/${project}_ci:${commit}"
        def String container = "${project}_ci_project_tests"
        if (!testArtifacts) {
            def folder = dockerReadEnv fullImageName, 'PROJECT_FOLDER'
            if (!folder) {
                throw new Exception("The PROJECT_FOLDER environment variable must be configured in the container image, for the projects test artifacts to be correctly located. Otherwise, specify the `testArtifacts` argument, for example `/srv/api-dummy/build/*.xml`")
            }
            testArtifacts = ["${folder}/build/*.xml"]
        }
        try {
            sh "docker rm ${container} || true"
            sh "docker run --name ${container} ${fullImageName}"
        } finally { 
            for (int i = 0; i < testArtifacts.size(); i++) {
                def remoteTestArtifact = new RemoteTestArtifact(testArtifacts.get(i))
                sh "docker cp ${container}:${remoteTestArtifact.remoteTestArtifactFolder()}/. ${remoteTestArtifact.localTestArtifactFolder()}"
                step([$class: 'JUnitResultArchiver', testResults: remoteTestArtifact.localTestArtifact()])
            }
            sh "docker rm ${container}"
        }
    }, 'project-tests', commit)
}
