def call(stackname, revision, concurrency='serial') {
    builderStart stackname

    withCommitStatus({
        sh "${env.BUILDER_PATH}bldr 'switch_revision_update_instance:${stackname},${revision},concurrency=${concurrency}'"
    }, 'deploy', revision)
}
