def call(project, tag='latest', Map configuration) {
    withCommitStatus({
        try {
            sh DockerCompose
                .command('up')
                .withEnvironment('IMAGE_TAG', tag)
                .withOption('d')
                .withOption('force-recreate')
                .toString()
            def waitFor = configuration.get('waitFor', [])
            for (int i = 0; i < waitFor.size(); i++) {
                sh "docker wait ${waitFor.get(i)}"
            }

            def scripts = configuration.get('scripts', {})
            scripts.each({ container, path ->
                sh DockerCompose
                    .command('exec')
                    .withEnvironment('IMAGE_TAG', tag)
                    .withOption('T')
                    .withArgument(container)
                    .withArgument(path)
                    .toString()
            })
        } finally {
            sh DockerCompose
                .command('down')
                .withEnvironment('IMAGE_TAG', tag)
                .toString()
        }
    }, 'smoke_tests', tag)
}
