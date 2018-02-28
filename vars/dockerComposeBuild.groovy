import DockerCompose

def call(tag='latest')
{
    sh DockerCompose
        .command('build')
        .withEnvironment('IMAGE_TAG', tag)
        .toString()
}
