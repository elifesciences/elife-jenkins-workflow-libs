def call() {
    return sh("git rev-parse HEAD", returnStdout=true).trim()
}
