def call(Closure body) {
    // env.BRANCH_NAME is only specified for multibranch projects
    // and by extension, organization folders
    // "PR-738"
    if (env.BRANCH_NAME != null) {
        def pieces = env.BRANCH_NAME.split("-")
        assert pieces.length == 2 : "${pieces} was expected to have 2 elements, the original string conforming to the `PR-...` template"
        assert pieces[0] == 'PR' : "First element of ${pieces} should be `PR`"
        int prNumber = pieces[1] as Integer
        print "prNumber: ${prNumber}"
        body(prNumber)
    } else {
        echo "Skipped actions because not on a pull request"
    }
}
