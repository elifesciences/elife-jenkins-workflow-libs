def call(Closure body) {
    node {
        timestamps {
            wrap([$class: 'AnsiColorBuildWrapper']) {
                try {
                    body()
                } catch (e) {
                    if (fileExists('maintainers.txt')) {
                        maintainersFile = readFile 'maintainers.txt'
                        maintainers = maintainersFile.tokenize("\n").collect { it.trim() }
                        def w = new StringWriter()
                        e.printStackTrace(new PrintWriter(w))
                        maintainers.each { address ->
                            mail subject: "failed with ${e.message}", to: address, body: "Failed: ${w}"
                        }
                    }
                    throw e
                } finally {
                    deleteDir()
                }
            }
        }
    }
}
