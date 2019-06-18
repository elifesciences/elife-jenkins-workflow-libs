def call(Closure body) {
    // env.TAG_NAME is only specified for multibranch projects
    // when a tag is built rather than a branch or a pull request
    // e.g. "0.0.1"
    if (env.TAG_NAME != null) {
        body(env.TAG_NAME)
    } else {
        echo "Skipped actions because not on a tag"
    }
}
