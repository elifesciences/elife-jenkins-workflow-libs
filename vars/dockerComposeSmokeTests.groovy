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

            def services = configuration.get('services', [:])
            // deprecated fallback
            if (!services) {
                services = configuration.get('scripts', [:])
            }
            // end of deprecated fallback
            services.each({ container, path ->
                sh DockerCompose
                    .command('exec', ['docker-compose.yml', 'docker-compose.ci.yml'])
                    .withEnvironment('IMAGE_TAG', tag)
                    .withOption('T')
                    .withArgument(container)
                    .withArgument(path)
                    .toString()
            })

            def blackbox = configuration.get('blackbox', [])
            blackbox.each({ path ->
                sh path
            })
        } finally {
            sh DockerCompose
                .command('down', ['docker-compose.yml', 'docker-compose.ci.yml'])
                .withEnvironment('IMAGE_TAG', tag)
                .withOption('volumes')
                .toString()
        }
    }, 'smoke_tests', tag)
}
