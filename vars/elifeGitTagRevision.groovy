def call(tag, revision = null) {
    if(revision != null) {
        sh(script: "git checkout ${revision}")
    }
    retval = sh(script:'git tag ${tag} && git push --tags', returnStatus: true)
    assert retval == 0 : "failed to tag repository with '${tag}'"
}
