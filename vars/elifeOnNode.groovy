def call(Closure body, nodeName='elife-libraries--ci') {
    lock(nodeName) {
        builderStart nodeName
        jenkinsCli "connect-node ${nodeName}"
        node(nodeName) {
            body()
            deleteDir()
        }
    }
}
