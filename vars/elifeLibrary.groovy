def call(Closure body, slave='elife-libraries--powerful') {
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


      
