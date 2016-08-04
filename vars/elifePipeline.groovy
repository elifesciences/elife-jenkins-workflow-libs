import com.cloudbees.groovy.cps.NonCPS

def findMaintainers(fileName) {
    if (fileExists(fileName)) {
        maintainersFile = readFile fileName
        maintainers = maintainersFile.tokenize("\n").collect { it.trim() }
        return maintainers
    }

    return []
}

def call(Closure body) {
    node {
        timestamps {
            wrap([$class: 'AnsiColorBuildWrapper']) {
                try {
                    body()
                } catch (e) {
                    def w = new StringWriter()
                    e.printStackTrace(new PrintWriter(w))
                    maintainers = findMaintainers 'maintainers.txt'
                    for (int i = 0; i < maintainers.size(); i++) {
                        def address = maintainers.get(i)
                        mail subject: "failed with ${e.message}", to: address, body: "Failed: ${w}"
                    }
                    throw e
                } finally {
                    deleteDir()
                }
            }
        }
    }
}
