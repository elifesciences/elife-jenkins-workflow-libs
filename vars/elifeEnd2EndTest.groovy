def call() {
    node("end2end") {
        sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}execute.sh"
        
        def end2endTestXmlArtifact = "${env.BUILD_TAG}.end2end.junit.xml"
        sh "cp ${env.SPECTRUM_PREFIX}build/junit.xml ${end2endTestXmlArtifact}"
        step([$class: "JUnitResultArchiver", testResults: end2endTestArtifact])

        def end2endTestLogArtifact = "${env.BUILD_TAG}.end2end.log"
        sh "cp ${env.SPECTRUM_PREFIX}build/test.log ${end2endTestLogArtifact}"
        archive $end2endTestLogArtifact
    }
}
