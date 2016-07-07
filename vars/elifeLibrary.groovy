def call(Closure body) {
    elifePipeline {
        node("libraries") {
            body()
            deleteDir()
        }
    }
}


      
