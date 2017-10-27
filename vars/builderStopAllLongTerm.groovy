def call(stacks, lockName = null) {
    def actions = [:]
    for (int i = 0; i < stacks.size(); i++) {
        def stack = stacks.get(i)
        actions[stack] = {
            lock(stack) {
                builderStop(stack, ['ec2', 'rds'])
            }
        }
    }
    parallel actions
}
