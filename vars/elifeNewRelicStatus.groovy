def call(applicationId) {
    return sh(script: "/usr/local/jenkins-scripts/new_relic_status.sh ${applicationId}", returnStdout: true).trim()
}
