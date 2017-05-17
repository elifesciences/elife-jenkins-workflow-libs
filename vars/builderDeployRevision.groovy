def call(stackname, revision, concurrency='serial') {
    builderStart stackname
    sh "${env.BUILDER_PATH}bldr 'switch_revision_update_instance:${stackname},${revision},concurrency=${concurrency}'"
}
