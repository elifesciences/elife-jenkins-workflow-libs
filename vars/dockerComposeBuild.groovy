import DockerCompose

def call(tag='latest')
{
    sh DockerCompose
        .command('build', ['docker-compose.yml', 'docker-compose.ci.yml'])
        .withEnvironment('IMAGE_TAG', tag)
        .toString()
}
