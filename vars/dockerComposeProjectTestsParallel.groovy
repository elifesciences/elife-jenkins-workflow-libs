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
                echo "Test artifacts: ${testArtifacts.keySet()}"
                testArtifacts.keySet().each({ k ->
                    echo k.getClass().toString()
                    echo label.getClass().toString()
                    echo label.equals(k).toString()
                })
                if (testArtifacts.containsKey(label)) {
                    def remoteTestArtifact = new RemoteTestArtifact(testArtifacts.get(label))
                    sh "docker cp ${container}:${remoteTestArtifact.remoteTestArtifactFolder()}/. ${remoteTestArtifact.localTestArtifactFolder()}"
                    step([$class: "JUnitResultArchiver", testResults: remoteTestArtifact.localTestArtifact()])
                } else {
                    echo "No test artifacts defined for ${label}"
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
