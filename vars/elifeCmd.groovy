def call(stackname, cmd) {
    sh "sudo -H -u elife ${env.BUILDER_SCRIPTS_PREFIX}cmd ${stackname},'${cmd}'"
}
