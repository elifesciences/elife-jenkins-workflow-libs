def call(stacks, cmd, concurrency='serial') {
    if (!(cmd instanceof List)) {
        cmd = [cmd]
    }
    def actions = [:]
    for (int i = 0; i < stacks.size(); i++) {
        def stack = stacks.get(i)
        actions[stack] = {
            lock(stack) {
                builderStart(stack)
                for (i = 0; i < cmd.size(); i++) { 
                    builderCmd(stack, cmd.get(i), null, false, concurrency)
                }
            }
        }
    }
    parallel actions
}
