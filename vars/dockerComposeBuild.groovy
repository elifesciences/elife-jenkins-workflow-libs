import DockerCompose

def call(tag='latest')
{
    withCommitStatus({
        sh "docker-wait-daemon"
        try {
            sh DockerCompose
                .command('build', ['docker-compose.yml', 'docker-compose.ci.yml'])
                // enable verbosity to debug frequent timeout failures
                .withGeneralOption('verbose')
                .withEnvironment('IMAGE_TAG', tag)
                .toString()
        } catch (Exception e) {
            echo "Exception class: "
            echo e.getClass().toString()
            echo "Exception message: "
            echo e.getMessage()
            throw e
        }
    }, 'build', tag)
}
