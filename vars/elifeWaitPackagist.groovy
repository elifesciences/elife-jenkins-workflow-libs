def call(packageName, revision) {
    message = message.replaceAll(/"/, '\\\\"')
    sh "timeout 60s /usr/local/jenkins-scripts/wait_packagist.sh ${packageName} ${revision}"
}
