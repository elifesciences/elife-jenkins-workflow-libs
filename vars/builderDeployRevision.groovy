def call(stackname, revision) {
    sh "${env.BUILDER_PATH}bldr 'switch_revision_update_instance:${stackname},${revision}'"
}
