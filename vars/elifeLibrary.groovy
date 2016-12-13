def call(Closure body, slave='elife-libraries--ci') {
    elifePipeline {
        lock(slave) {
            builderStart slave
            jenkinsCli "connect-node ${slave}"
            node("libraries") {
                body()
                deleteDir()
            }
        }
    }
}


      
