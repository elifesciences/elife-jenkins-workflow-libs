def call(testArtifact, minimumNumber) {
    sh script: "/usr/local/jenkins-scripts/countjunitxml.py ${testArtifact} ${minimumNumber}"
}
