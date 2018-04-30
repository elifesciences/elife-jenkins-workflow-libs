import DockerCompose

def call(project, tag='latest', testArtifacts=[:])
{
    action = { stackname, command, folder, label ->
        withCommitStatus({
            def String container = "${project}_ci_${label}"
            try {
                sh "docker rm ${container} || true"

                sh DockerCompose
                    .command('run', ['docker-compose.yml', 'docker-compose.ci.yml'])
                    .withEnvironment('IMAGE_TAG', tag)
                    .withOption('name', container)
                    .withArgument('ci')
                    .withArgument(command)
                    .toString()
            } finally {
                if (testArtifacts.containsKey(label)) {
                    def remoteTestArtifact = new RemoteTestArtifact(testArtifacts.get(name))
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
    parallel actions
}
