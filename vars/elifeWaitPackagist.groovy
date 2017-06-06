def call(packageName, revision) {
    sh "timeout 60s /usr/local/jenkins-scripts/wait_packagist.sh ${packageName} ${revision}"
}
