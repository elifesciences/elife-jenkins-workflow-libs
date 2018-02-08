def call(imageName, variableName) {
    return sh(script: "docker-read-env ${imageName} ${variableName}", returnStdout: true).trim()
}
