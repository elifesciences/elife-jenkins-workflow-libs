def findMaintainers(fileName) {
    def maintainers = []

    if (fileExists(fileName)) {
        echo "Found maintainers file ${fileName}" 
        def maintainersFile = readFile fileName
        def rows = maintainersFile.tokenize("\n")
        for (int i = 0; i < rows.size(); i++) {
            maintainer = rows.get(i).trim()
            maintainers << maintainer
        }
    } else {
        echo "No maintainers.txt file found"
    }

    return maintainers
}

def call(Closure body) {
    node {
        timestamps {
            wrap([$class: 'AnsiColorBuildWrapper']) {
                // TODO: revise when Declarative Pipeline is available
                try {
                    timeout(time:60, unit:'MINUTES') {
                        body()
                    }
                } catch (e) {
                    maintainers = findMaintainers 'maintainers.txt'
                    echo "Found maintainers: ${maintainers}"
                    for (int i = 0; i < maintainers.size(); i++) {
                        def address = maintainers.get(i)
                        mail subject: "${env.BUILD_TAG} failed", to: address, from: "alfred@elifesciences.org", replyTo: "no-reply@elifesciences.org", body: "Message: ${e.message}\nFailed build: ${env.BUILD_URL}console"
                        echo "Failure email sent to ${address}"
                    }
                    throw e
                } finally {
                    deleteDir()
                }
            }
        }
    }
}
