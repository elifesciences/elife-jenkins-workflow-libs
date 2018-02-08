def call(project, commit, /*testArtifacts=[],*/ organization='elifesciences') {
    def fullImageName = "${organization}/${project}_ci:${commit}"
    def folder = dockerReadEnv fullImageName, 'PROJECT_FOLDER'
    sh "chmod 777 build/ && docker run -v \$(pwd)/build:${folder}/build ${fullImageName}"
    step([$class: 'JUnitResultArchiver', testResults: 'build/*.xml'])
}
