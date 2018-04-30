import DockerCompose

def call(project, tag='latest', testArtifacts=[])
{
    action = { stackname, command ->
        withCommitStatus({
            def String container = "${project}_ci_project_tests"
            try {
                sh "docker rm ${container} || true"

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
                    sh "docker cp ${container}:${remoteTestArtifact.remoteTestArtifactFolder()}/. ${remoteTestArtifact.localTestArtifactFolder()}"
                    step([$class: "JUnitResultArchiver", testResults: remoteTestArtifact.localTestArtifact()])
                }
                sh "docker rm ${container}"
            }
        }, 'project-tests', tag)
    }
    def actions = _defineProjectTests('unused', '/srv/journal', action)
    actions.each({ n, v -> 
        echo "Name of action: ${n}"
    })
}
