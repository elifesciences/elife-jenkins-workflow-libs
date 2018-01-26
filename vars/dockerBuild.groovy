def call(project, tag='latest', organization='elifesciences') {
    def imageName = "${organization}/${project}"
    sh "docker build --pull -t ${imageName}:${tag} ."
    //return new DockerImage(this)
}
