def call() {
    return sh("git rev-parse HEAD > ${commitFile}", returnStdout=true).trim()
}
