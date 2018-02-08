def call(project, commit, /*testArtifacts=[],*/ organization='elifesciences') {
    def imageName = "${organization}/${project}_ci"
    def folder = dockerReadEnv imageName, 'PROJECT_FOLDER'
    sh "chmod 777 build/ && docker run -v \$(pwd)/build:${folder}/build ${imageName}:${commit}"
    step([$class: 'JUnitResultArchiver', testResults: 'build/*.xml'])
}
