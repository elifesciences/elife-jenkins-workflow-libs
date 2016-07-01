def call(stackname, cmd) {
    sh "sudo -H -u elife ${env.OLD_BUILDER_SCRIPTS_PREFIX}cmd '${stackname},${cmd}'"
}
