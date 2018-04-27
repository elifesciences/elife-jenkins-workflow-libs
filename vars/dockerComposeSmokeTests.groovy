def call(project, tag='latest', Map configuration) {
    withCommitStatus({
        try {
            sh DockerCompose
                .command('up', ['docker-compose.yml', 'docker-compose.ci.yml'])
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
                    .command('exec', ['docker-compose.yml', 'docker-compose.ci.yml'])
                    .withEnvironment('IMAGE_TAG', tag)
                    .withOption('T')
                    .withArgument(container)
                    .withArgument(path)
                    .toString()
            })
        } finally {
            sh DockerCompose
                .command('down', ['docker-compose.yml', 'docker-compose.ci.yml'])
                .withEnvironment('IMAGE_TAG', tag)
                .toString()
        }
    }, 'smoke_tests', tag)
}
