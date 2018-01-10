def call() {
    return sh('date +"%Y%m%d%H%M%S"', returnStdout: true)
}
