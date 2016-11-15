def call(Closure body) {
    elifePipeline {
        lock("elife-libraries--powerful") {
            builderStart "elife-libraries--powerful"
            jenkinsCli "connect-node libraries-runner"
            node("libraries") {
                body()
                deleteDir()
            }
        }
    }
}


      
