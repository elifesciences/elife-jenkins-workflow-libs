def call(stackname, minutes) {
    sh "${env.BUILDER_PATH}bldr 'stop_if_running_for:${stackname},${minutes}'"
}
