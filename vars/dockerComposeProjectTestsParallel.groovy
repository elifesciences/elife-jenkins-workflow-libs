import DockerCompose

def call(project, tag='latest', testArtifacts=[:])
{
    action = { stackname, command, folder, label ->
        def String container = "${project}_ci_${label}"
        try {
            sh "docker rm ${container} --volumes || true"

            sh DockerCompose
                .command('run', ['docker-compose.yml', 'docker-compose.ci.yml'])
                .withEnvironment('IMAGE_TAG', tag)
                .withOption('name', container)
                .withArgument('ci')
                .withArgument(command)
                .toString()
        } finally {
            if (testArtifacts.containsKey(label)) {
                def remoteTestArtifact = new RemoteTestArtifact(testArtifacts.get(label))
                sh "mkdir -p ${remoteTestArtifact.localTestArtifactFolder()}"
                sh "docker cp ${container}:${remoteTestArtifact.remoteTestArtifactFolder()}/. ${remoteTestArtifact.localTestArtifactFolder()}"
                step([$class: "JUnitResultArchiver", testResults: remoteTestArtifact.localTestArtifact()])
            } else {
                echo "No test artifacts defined for ${label}"
            }
            sh "docker rm ${container} --volumes"
        }
    }
    def actions = _defineProjectTests('unused', '/srv/journal', action)
    try {
        parallel actions
    } finally {
        sh DockerCompose
            .command('down', ['docker-compose.yml', 'docker-compose.ci.yml'])
            .withEnvironment('IMAGE_TAG', tag)
            .withOption('volumes')
            .toString()
    }
}
