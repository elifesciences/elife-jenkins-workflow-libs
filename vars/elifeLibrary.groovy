def call(Closure body, slave='elife-libraries--ci', timeoutInMinutes=120) {
    elifePipeline({
        lock(slave) {
            builderStart slave
            jenkinsCli "connect-node ${slave}"
            node(slave) {
                body()
                deleteDir()
            }
        }
    }, 120)
}


      
