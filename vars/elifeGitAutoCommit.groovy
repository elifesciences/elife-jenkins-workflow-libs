def call(message = "Automatic commit", add=".") {
    sh "git add --all --force ${add}"
    def code = sh script: "git diff --quiet --exit-code --cached", returnStatus: true
    echo "git diff exit code: ${code}"
    if (code) {
        sh "git commit -m \"${message}\""
        return true
    } else{
        echo "No diff to commit"
        return false
    }
}
