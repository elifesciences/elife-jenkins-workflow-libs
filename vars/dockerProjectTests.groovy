def call(project, commit, folder, /*testArtifacts=[],*/ organization='elifesciences') {
    def imageName = "${organization}/${project}_ci"
    sh "chmod 777 build/ && docker run -v \$(pwd)/build:${folder}/build ${imageName}:${commit}"
    step([$class: "JUnitResultArchiver", testResults: 'build/*.xml'])
}
