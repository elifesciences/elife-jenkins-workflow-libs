def call(project, tag='latest', Map configuration) {
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

    def smokeTests = configuration.get('smokeTests', {})
    smokeTests.each({ container, path ->
        sh DockerCompose
            .command('exec')
            .withEnvironment('IMAGE_TAG', tag)
            .withOption('T')
            .withArgument(container)
            .withArgument(path)
            .toString()
    })
    // finally docker-compose stop? or down, but only if we can still do `docker cp` afterwards, unlikely but since it's on a different container not in the list...
}
