import Stack

def call(stackname, revision, concurrency='serial') {
    def stack = Stack.fromName(stackname)
    builderStart stackname

    withCommitStatus({
        sh "${env.BUILDER_PATH}bldr 'switch_revision_update_instance:${stackname},${revision},concurrency=${concurrency}'"
    }, "${stack.environment()}/deploy", revision)
}
