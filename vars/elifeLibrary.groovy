def call(Closure body) {
    elifePipeline {
        lock("elife-libraries--ci") {
            builderStart "elife-libraries--ci"
            node("libraries") {
                body()
                deleteDir()
            }
        }
    }
}


      
