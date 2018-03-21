def call(imageName, labelName) {
    return sh(script: "/usr/local/docker-scripts/docker-read-label ${imageName} ${labelName}", returnStdout: true).trim()
}
