// TODO: rename in elifeDeploy
def call(stackname, commit) {
    sh "sudo -H -u elife ${env.OLD_BUILDER_SCRIPTS_PREFIX}switch_revision_update_instance ${stackname},${commit}"
}
