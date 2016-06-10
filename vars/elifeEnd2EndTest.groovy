def call() {
    node("end2end") {
        sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}execute.sh"
        def end2endTestArtifact = "${env.BUILD_TAG}.end2end.junit.xml"
        sh "cp ${env.SPECTRUM_PREFIX}build/junit.xml ${end2endTestArtifact}"
        step([$class: "JUnitResultArchiver", testResults: end2endTestArtifact])
    }
}
