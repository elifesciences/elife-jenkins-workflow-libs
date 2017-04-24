def call(stackname) {
    sh "LOG_LEVEL_FILE=DEBUG ${env.BUILDER_PATH}bldr 'start:${stackname}'"
}
