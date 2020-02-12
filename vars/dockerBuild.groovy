import Docker

def call(project, tag='latest', dockerfileSuffix = null, organization='elifesciences', buildArgs=[:]) {
    sh "docker-wait-daemon"
    def imageName = "${organization}/${project}"
    def dockerfile = 'Dockerfile'
    if (dockerfileSuffix) {
        dockerfile = "${dockerfile}.${dockerfileSuffix}"
    }
    sh Docker.command('build')
        .withOption('pull')
        .withOption('f', dockerfile)
        .withOption('t', "${imageName}:${tag}")
        .withOption('build-arg', buildArgs)
        .withArgument('.')
        .toString()
}
