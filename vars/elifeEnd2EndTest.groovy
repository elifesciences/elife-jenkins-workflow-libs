def call(Closure preliminaryStep=null) {
    node("end2end") {
        if (preliminaryStep != null) {
            preliminaryStep()
        }
        sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}execute.sh || echo TESTS FAILED"
        
        def end2endTestXmlArtifact = "${env.BUILD_TAG}.end2end.junit.xml"
        sh "cp ${env.SPECTRUM_PREFIX}build/junit.xml ${end2endTestXmlArtifact}"
        elifeTestArtifact end2endTestXmlArtifact

        def end2endTestLogArtifact = "${env.BUILD_TAG}.end2end.log"
        sh "cp ${env.SPECTRUM_PREFIX}build/test.log ${end2endTestLogArtifact}"
        archive end2endTestLogArtifact

        elifeVerifyJunitXml end2endTestXmlArtifact
    }
}
