def call(Closure body) {
    // env.BRANCH_NAME is only specified for multibranch projects
    // and by extension, organization folders
    if (env.BRANCH_NAME != null) {
        body()
    } else {
        echo "Skipped actions because not on a pull request"
    }
}
