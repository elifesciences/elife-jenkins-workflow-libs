def call(message = "Automatic commit", add=".") {
    sh "git add --all --force ${add}"
    def code = sh script: "git diff --quiet --exit-code --cached", returnStatus: true
    if (code) {
        "git commit -m \"${message}\""
        return true
    } else{
        return false
    }
}
