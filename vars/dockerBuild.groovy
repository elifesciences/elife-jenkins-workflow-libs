def call(project, tag='latest', dockerfileSuffix = null, organization='elifesciences', buildArgs=[:]) {
    sh "docker-wait-daemon"
    def imageName = "${organization}/${project}"
    def dockerfile = 'Dockerfile'
    if (dockerfileSuffix) {
        dockerfile = "${dockerfile}.${dockerfileSuffix}"
    }
    def buildArgsOption = ''
    for (def argName in buildArgs) {
        buildArgsOption += " --build-arg ${argName}=${buildArgs[argName]}"
    }
    sh "docker build --pull -f ${dockerfile} -t ${imageName}:${tag} .${buildArgsOption}"
    //return new DockerImage(this)
}
