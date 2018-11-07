def call(command, returnStdout=false) {
    return sh(script: "${env.JENKINS_CLI} ${command}", returnStdout: returnStdout)
}
