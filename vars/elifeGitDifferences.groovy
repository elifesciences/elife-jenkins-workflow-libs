def call(subfolder='.') {
    return sh(script: "git diff --cached --exit-code ${subfolder}", returnStatus: true)
}
