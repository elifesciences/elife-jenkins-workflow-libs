def call(stacks, lockName = null) {
    def actions = [:]
    for (int i = 0; i < stacks.size(); i++) {
        def stack = stacks.get(i)
        if (lockName) {
            actions[stack] = {
                builderStart(stack)
            }
        } else {
            actions[stack] = {
                lock(stack) {
                    builderStart(stack)
                }
            }
        }
    }
    if (lockName) {
        lock (lockName) {
            parallel actions
        }
    } else {
        parallel actions
    }
}
