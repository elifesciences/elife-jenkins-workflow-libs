def call(packageName, revision) {
    sh "timeout 5m /usr/local/jenkins-scripts/wait_packagist.sh ${packageName} ${revision}"
}
