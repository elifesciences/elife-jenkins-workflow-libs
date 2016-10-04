def call(stacks, lockName = null) {
    def actions = [:]
    for (int i = 0; i < stacks.size(); i++) {
        def stack = stacks.get(i)
        if (lockName) {
            actions[stack] = {
                builderStopIfNextHourIsImminent(stack)
            }
        } else {
            actions[stack] = {
                lock(stack) {
                    builderStopIfNextHourIsImminent(stack)
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
