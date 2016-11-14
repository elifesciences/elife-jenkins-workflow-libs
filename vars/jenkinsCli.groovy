def call(command) {
    sh "${env.JENKINS_CLI} ${command}"
}
