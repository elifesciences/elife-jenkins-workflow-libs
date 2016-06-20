def call(testArtifact) {
    sh "/usr/local/jenkins-scripts/verifyjunitxml.py ${testArtifact}"
}
