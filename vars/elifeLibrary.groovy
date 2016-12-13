def call(Closure body, slave='elife-libraries--ci') {
    elifePipeline {
        lock(slave) {
            builderStart slave
            jenkinsCli "connect-node ${slave}"
            node(slave) {
                body()
                deleteDir()
            }
        }
    }
}


      
