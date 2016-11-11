def call(Closure body) {
    elifePipeline {
        builderStart "elife-libraries--ci"
        node("libraries") {
            body()
            deleteDir()
        }
    }
}


      
