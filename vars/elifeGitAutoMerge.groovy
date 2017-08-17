def call(branch, title, description = '', base='origin/master') {
    def pieces = base.split("-")
    assert pieces.length == 2 : "${pieces} was expected to have 2 elements, the original string conforming to the `origin/...` template"
    assert pieces[0] == 'origin' : "First element of ${pieces} should be `origin`"
    def localBase = pieces[1]
    sh "git checkout ${localBase}"
    sh "git pull origin ${localBase}"
    sh "git merge ${branch} -m ${title}"
    sh "git push origin ${localBase}"
}
