def call(project, commit, organization='elifesciences') {
    def imageName = "${organization}/${project}"
    sh "docker build -f Dockerfile.ci -t ${imageName}:${commit} --build-arg commit=${commit} ."
}
