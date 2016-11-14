def call(Closure body) {
    elifePipeline {
        lock("elife-libraries--ci") {
            builderStart "elife-libraries--ci"
            jenkinsCli "connect-node libraries-runner"
            node("libraries") {
                body()
                deleteDir()
            }
        }
    }
}


      
