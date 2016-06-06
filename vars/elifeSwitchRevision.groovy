def call(stackname, commit) {
    sh "sudo -H -u elife ${env.BUILDER_SCRIPTS_PREFIX}switch_revision ${stackname},${commit}"
}
