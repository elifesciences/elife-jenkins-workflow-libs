def call(project, tag='latest', dockerfileSuffix = null, organization='elifesciences') {
    sh "docker-wait-daemon"
    def imageName = "${organization}/${project}"
    def dockerfile = 'Dockerfile'
    if (dockerfileSuffix) {
        dockerfile = "${dockerfile}.${dockerfileSuffix}"
    }
    sh "docker build --pull -f ${dockerfile} -t ${imageName}:${tag} ."
    //return new DockerImage(this)
}
