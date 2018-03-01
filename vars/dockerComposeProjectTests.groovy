import DockerCompose

def call(project, tag='latest', testArtifacts=[])
{
    try {
        def String container = "${project}_ci_project_tests"
        sh "docker rm ${container} || true"

        sh DockerCompose
            .command('run')
            .withEnvironment('IMAGE_TAG', tag)
            .withOption('name', container)
            .withArgument('ci')
            .withArgument('./project_tests.sh')
            .toString()
    } finally {
        for (int i = 0; i < testArtifacts.size(); i++) {
            def remoteTestArtifact = new RemoteTestArtifact(testArtifacts.get(i))
            sh "docker cp ${container}:${remoteTestArtifact.path()} ${remoteTestArtifact.localTestArtifactFolder()}"
            step([$class: "JUnitResultArchiver", testResult: remoteTestArtifact.localTestArtifact()])
        }
        sh "docker rm ${container}"
    }
}
