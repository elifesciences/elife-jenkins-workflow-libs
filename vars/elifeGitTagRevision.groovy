def call(tag, revision = null) {
    if(revision != null) {
        sh(script: "git checkout ${revision}")
    }
    return sh(script: "git tag ${tag} && git push --tags")
}
