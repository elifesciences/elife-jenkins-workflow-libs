def call(testArtifact) {
    def failedNumber = sh script: "/usr/local/jenkins-scripts/verifyjunitxml.py ${testArtifact}", returnStatus: true
    if (failedNumber != 0) {
        error("${failedNumber} tests failed");
    }
}
