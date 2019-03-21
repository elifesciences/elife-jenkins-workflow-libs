def call(message, channel) {
    message = message.replaceAll(/"/, '\\\\"')
    sh "/usr/local/jenkins-scripts/notify_slack.sh \"${message}\" \"${channel}\""
}
