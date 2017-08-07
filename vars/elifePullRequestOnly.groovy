def call(Closure body) {
    // env.BRANCH_NAME is only specified for multibranch projects
    // and by extension, organization folders
    if (env.BRANCH_NAME != null) {
        if (body.maximumNumberOfParameters == 0) {
            body()
        } else {
            // env.BUILD_TAG
            // jenkins-pull-requests-projects-pattern-library-PR-737-1
            def prNumber = 42;
            body(prNumber);
        }
    } else {
        echo "Skipped actions because not on a pull request"
    }
}
