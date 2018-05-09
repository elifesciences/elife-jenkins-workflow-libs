import DockerCompose

def call(project, tag='latest', testArtifacts=[])
{
    withCommitStatus({
        def String container = "${project}_ci_project_tests"
        try {
            sh "docker rm ${container} --volumes || true"

            sh DockerCompose
                .command('run', ['docker-compose.yml', 'docker-compose.ci.yml'])
                .withEnvironment('IMAGE_TAG', tag)
                .withOption('name', container)
                .withArgument('ci')
                .withArgument('./project_tests.sh')
                .toString()
        } finally {
            for (int i = 0; i < testArtifacts.size(); i++) {
                def remoteTestArtifact = new RemoteTestArtifact(testArtifacts.get(i))
                sh "mkdir -p ${remoteTestArtifact.localTestArtifactFolder()}"
                sh "docker cp ${container}:${remoteTestArtifact.remoteTestArtifactFolder()}/. ${remoteTestArtifact.localTestArtifactFolder()}"
                step([$class: "JUnitResultArchiver", testResults: remoteTestArtifact.localTestArtifact()])
            }
            sh "docker rm ${container} --volumes"
            sh DockerCompose
                .command('down', ['docker-compose.yml', 'docker-compose.ci.yml'])
                .withEnvironment('IMAGE_TAG', tag)
                .withOption('volumes')
                .toString()
        }
    }, 'project-tests', tag)
}
