def call(packageName, revision) {
    // "The search index is updated every five minutes. 
    // It will index (or reindex) any package that has been crawled since the last time the search indexer ran."
    // - https://packagist.org/about
    sh "timeout 5m /usr/local/jenkins-scripts/wait_packagist.sh ${packageName} ${revision}"
}
