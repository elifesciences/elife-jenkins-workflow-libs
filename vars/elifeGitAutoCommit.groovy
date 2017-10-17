def call(message = "Automatic commit", String... add) {
    if (add.length > 0) {
        path = add.join(" ")
    } else {
        path = "."
    }
    sh "git add --all --force ${path}"
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
