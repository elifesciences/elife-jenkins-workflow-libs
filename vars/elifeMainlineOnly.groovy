def call(Closure body) {
    // env.BRANCH_NAME is only specified for multibranch projects
    // and by extension, organization folders
    // env.TAG_NAME is also specified for tags, but this check already excludes them
    // since they have env.BRANCH_NAME
    if (env.BRANCH_NAME == null) {
        body()
    } else {
        echo "Skipped actions because not on the mainline"
    }
}
