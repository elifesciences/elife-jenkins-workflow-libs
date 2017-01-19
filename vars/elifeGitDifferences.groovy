def call() {
    return sh(script: "git diff --cached --exit-code", returnStatus: true)
}
