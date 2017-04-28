def call(stackname) {
    builderStart stackname
    sh "${env.BUILDER_PATH}bldr 'update:${stackname}'"
}
