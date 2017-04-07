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
                    timeout(time:120, unit:'MINUTES') {
                        elifePipelineEvent(
                            pipeline: env.JOB_NAME,
                            type: 'pipeline-start',
                            number: env.BUILD_NUMBER
                            // not always available, e.g. in branches?
                            // commit: env.GIT_COMMIT 
                        )
                        body()
                        elifePipelineEvent(
                            pipeline: env.JOB_NAME,
                            type: 'pipeline-success',
                            number: env.BUILD_NUMBER
                            // not always available, e.g. in branches?
                            // commit: env.GIT_COMMIT 
                        )
                    }
                    // delete workspace
                    deleteDir()
                } catch (e) {
                    elifePipelineEvent(
                        pipeline: env.JOB_NAME,
                        type: 'pipeline-failure',
                        number: env.BUILD_NUMBER
                        // not always available, e.g. in branches?
                        // commit: env.GIT_COMMIT 
                    )
                    maintainers = findMaintainers 'maintainers.txt'
                    echo "Found maintainers: ${maintainers}"
                    for (int i = 0; i < maintainers.size(); i++) {
                        def address = maintainers.get(i)
                        mail subject: "${env.BUILD_TAG} failed", to: address, from: "alfred@elifesciences.org", replyTo: "no-reply@elifesciences.org", body: "Message: ${e.message}\nFailed build: ${env.BUILD_URL}console"
                        echo "Failure email sent to ${address}"
                    }
                    throw e
                }
            }
        }
    }
}
