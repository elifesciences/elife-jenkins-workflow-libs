def call(stackname, revision) {
    builderStart stackname
    sh "${env.BUILDER_PATH}bldr 'update:${stackname}'"
}
