def call(stackname, minutes) {
    sh "${env.BUILDER_PATH}bldr 'stop_running_for:${stackname},${minutes}'"
}
