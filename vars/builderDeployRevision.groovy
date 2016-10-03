def call(stackname, revision) {
    builderStart stackname
    sh "${env.BUILDER_PATH}bldr 'switch_revision_update_instance:${stackname},${revision}'"
}
