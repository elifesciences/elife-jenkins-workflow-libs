def findMaintainers(fileName) {
    def maintainers = []

    if (fileExists(fileName)) {
        echo "Found maintainers file ${fileName}" 
        def maintainersFile = readFile fileName
        echo "File content is `${maintainersFile}`"
        def rows = maintainersFile.tokenize("\n")
        for (int i = 0; i < rows.size(); i++) {
            maintainer = rows.get(i).trim()
            maintainers << maintainer
        }
    }

    return maintainers
}

def call(Closure body) {
    node {
        timestamps {
            wrap([$class: 'AnsiColorBuildWrapper']) {
                try {
                    body()
                } catch (e) {
                    maintainers = findMaintainers 'maintainers.txt'
                    echo "Found maintainers: ${maintainers}"
                    for (int i = 0; i < maintainers.size(); i++) {
                        def address = maintainers.get(i)
                        echo "When configured, we will send an email like subject: \"${env.BUILD_TAG} failed\", to: address, body: \"Message: ${e.message}\nFailed build: ${env.BUILD_URL}"
                        mail subject: "${env.BUILD_TAG} failed", to: address, from: "alfred@elifesciences.org", replyTo: "no-reply@elifesciences.org", body: "Message: ${e.message}\nFailed build: ${env.BUILD_URL}"
                    }
                    throw e
                } finally {
                    deleteDir()
                }
            }
        }
    }
}
