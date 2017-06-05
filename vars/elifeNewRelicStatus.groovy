def call(applicationId) {
    return sh(script: "/usr/local/jenkins-scripts/new-relic-status.sh ${applicationId}", returnStdout: true).trim()
}
