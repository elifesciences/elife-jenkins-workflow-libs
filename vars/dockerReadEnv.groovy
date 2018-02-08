def call(imageName, variableName) {
    return sh(script: "/usr/local/docker-scripts/docker-read-env ${imageName} ${variableName}", returnStdout: true).trim()
}
