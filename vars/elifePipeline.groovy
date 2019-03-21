import Notification

def findMaintainers(fileName) {
    def maintainers = []

    // TODO: what is there is no .git repository? Do we use in-line Jenkinsfile?
    // we probably need to do this all the time, but careful not to mess with files being written by the build? Those files should probably be in .gitignore anyway
    echo "Checking out .git repository to make sure we find maintainers"
    checkout scm

    if (fileExists(fileName)) {
        echo "Found maintainers file ${fileName}" 
        def maintainersFile = readFile fileName
        def rows = maintainersFile.tokenize("\n")
        for (int i = 0; i < rows.size(); i++) {
            maintainer = rows.get(i).trim()
            maintainers << maintainer
        }
        echo "Found maintainers: ${maintainers}"
    } else {
        echo "No maintainers.txt file found"
    }

    return maintainers
}

def call(Closure body, timeoutInMinutes=120) {
    node {
        timestamps {
            wrap([$class: 'AnsiColorBuildWrapper']) {
                // TODO: revise when Declarative Pipeline is available
                try {
                    timeout(time:timeoutInMinutes, unit:'MINUTES') {
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
                    for (int i = 0; i < maintainers.size(); i++) {
                        def notification = Notification.fromMaintainersFileValue(maintainers.get(i))
                        if (notification.type() == Notification.EMAIL) {
                            mail subject: "${env.BUILD_TAG} failed", to: notification.value(), from: "alfred@elifesciences.org", replyTo: "no-reply@elifesciences.org", body: "Message: ${e.message}\nFailed build: ${env.RUN_DISPLAY_URL}"
                            echo "Failure email sent to ${notification.value()}"
                        } else if (notification.type() == Notification.SLACK) {
                            def slackMessage = "*${env.BUILD_TAG}* failed: ${e.message} (<${env.RUN_DISPLAY_URL}|Build>, <${env.RUN_CHANGES_DISPLAY_URL}|Changes>)"
                            elifeSlack slackMessage, notification.value()
                            echo "Slack notification sent to ${notification.value()}"
                        }
                    }
                    throw e
                }
            }
        }
    }
}
