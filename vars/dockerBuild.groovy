def call(project, tag='latest', dockerfileSuffix = null, organization='elifesciences', buildArgs=[:]) {
    sh "docker-wait-daemon"
    def imageName = "${organization}/${project}"
    def dockerfile = 'Dockerfile'
    if (dockerfileSuffix) {
        dockerfile = "${dockerfile}.${dockerfileSuffix}"
    }
    def buildArgsOption = ''
    for (argName in buildArgs) {
        buildArgsOption += " --build-arg ${argName.key}=${argName.value}"
    }
    sh "docker build --pull -f ${dockerfile} -t ${imageName}:${tag} .${buildArgsOption}"
    //return new DockerImage(this)
}
