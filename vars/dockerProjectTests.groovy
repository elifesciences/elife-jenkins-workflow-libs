def call(project, commit, /*testArtifacts=[],*/ organization='elifesciences') {
    def fullImageName = "${organization}/${project}_ci:${commit}"
    def folder = dockerReadEnv fullImageName, 'PROJECT_FOLDER'
    if (!folder) {
        throw new Exception("The PROJECT_FOLDER environment variable must be configured in the container image, for the projects tests to be correctly located.")
    }
    sh "chmod 777 build/ && docker run -v \$(pwd)/build:${folder}/build ${fullImageName}"
    step([$class: 'JUnitResultArchiver', testResults: 'build/*.xml'])
}
