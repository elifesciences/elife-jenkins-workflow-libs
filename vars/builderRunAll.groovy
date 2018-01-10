def call(stacks, cmd, branch=null, stackConcurrency='serial') {
    if (!(cmd instanceof List)) {
        cmd = [cmd]
    }
    def actions = [:]
    for (int i = 0; i < stacks.size(); i++) {
        def stack = stacks.get(i)
        actions[stack] = {
            lock(stack) {
                builderStart(stack)
                if (branch) {
                    sh "${env.BUILDER_PATH}bldr 'buildvars.switch_revision:${stack},${branch},concurrency=${stackConcurrency}'"
                }
                for (int j = 0; j < cmd.size(); j++) { 
                    builderCmd(stack, cmd.get(j), null, false, stackConcurrency)
                }
            }
        }
    }
    parallel actions
}
