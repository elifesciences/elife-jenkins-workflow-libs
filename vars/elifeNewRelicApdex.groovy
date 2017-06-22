def call(applicationId) {
    return Float.parseFloat(sh(script: "/usr/local/jenkins-scripts/new_relic_apdex.sh ${applicationId}", returnStdout: true).trim())
}
