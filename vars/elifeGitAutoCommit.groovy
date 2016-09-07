def call(message = "Automatic commit", add=".") {
    sh "git add ${add}"
    sh "git diff --quiet --exit-code --cached || git commit -m \"${message}\""
}
