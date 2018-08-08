import DockerCompose

def call(tag='latest')
{
    sh "docker-wait-daemon"
    sh DockerCompose
        .command('build', ['docker-compose.yml', 'docker-compose.ci.yml'])
        // enable verbosity to debug frequent timeout failures
        .withGeneralOption('verbose')
        .withEnvironment('IMAGE_TAG', tag)
        .toString()
}
