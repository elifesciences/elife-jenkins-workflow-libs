def call(project, commit, organization='elifesciences') {
    def imageName = "${organization}/${project}"
    sh "docker build -f Dockerfile.ci -t ${imageName}_ci:${commit} --build-arg commit=${commit} ."
}
