def call(tag, revision = null) {
    assert (tag != null) : "given tag was null."
    tag = String.valueOf(tag).trim()
    assert (tag != "") : "given tag was empty."
    if (!revision) {
        revision = elifeGitRevision()
    }
    retval = sh(script: "git tag | grep ${tag}", returnStatus: true)
    if(retval == 0) {
        echo("skipping tag and release: '${tag}' already exists")
        return null
    }
    retval = sh(script: "git checkout master && git tag ${tag} ${revision} && git push origin --tags", returnStatus: true)
    assert retval == 0 : "failed to tag repository with '${tag}'"
    return [tag, revision]
}
