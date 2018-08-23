import DockerCompose

def call(service, command, tag='latest')
{
    try {
        return sh(
            script: DockerCompose
                .command('run', ['docker-compose.yml', 'docker-compose.ci.yml'])
                .withEnvironment('IMAGE_TAG', tag)
                .withArgument(service)
                .withArgument(command)
                .toString(),
            returnStdout: true
        )
    } finally {
        sh DockerCompose
            .command('down', ['docker-compose.yml', 'docker-compose.ci.yml'])
            .withEnvironment('IMAGE_TAG', tag)
            .withOption('volumes')
            .toString()
    }
}
