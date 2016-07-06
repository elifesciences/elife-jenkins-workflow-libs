def call(message) {
    message = message.replaceAll(/"/, '\\\\"')
    sh "/usr/local/jenkins-scripts/notify_slack.sh \"${message}\""
}
