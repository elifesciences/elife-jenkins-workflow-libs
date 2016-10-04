def call(stacks, lockName = null) {
    def actions = [:]
    for (int i = 0; i < stacks.size(); i++) {
        def stack = stacks.get(i)
        actions[stack] = {
            lock(stack) {
                builderStart(stack)
            }
        }
    }
    parallel actions
}
