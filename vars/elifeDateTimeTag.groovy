def call() {
    return sh(script: 'date +"%Y%m%d%H%M%S"', returnStdout: true).trim()
}
