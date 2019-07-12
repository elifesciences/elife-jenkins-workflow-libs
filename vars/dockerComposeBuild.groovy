import DockerCompose

def call(tag='latest', Map options=[:])
{
    withCommitStatus({
        sh "docker-wait-daemon"
        try {
            def command = DockerCompose
                .command('build', ['docker-compose.yml', 'docker-compose.ci.yml'])
                // enable verbosity to debug frequent timeout failures
                .withGeneralOption('verbose')
            if (options.get('parallel', false)) {
                command = command.withOption('parallel')
            }
            if (options.get('no-cache', false)) {
                command = command.withOption('no-cache')
            }
            command = command
                .withEnvironment('IMAGE_TAG', tag)
                .toString()
            sh command
        } catch (Exception e) {
            echo "Exception class: "
            echo e.getClass().toString()
            echo "Exception message: "
            echo e.getMessage()
            throw e
        }
    }, 'build', tag)
}
