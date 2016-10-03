def call(stackname) {
    sh "${env.BUILDER_PATH}bldr 'stop_if_next_hour_is_imminent:${stackname},55'"
}
