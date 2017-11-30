def call(stacks, cmd, concurrency='serial') {
    def actions = [:]
    for (int i = 0; i < stacks.size(); i++) {
        def stack = stacks.get(i)
        actions[stack] = {
            lock(stack) {
                builderStart(stack)
                builderCmd(stack, cmd, concurrency)
            }
        }
    }
    parallel actions
}
