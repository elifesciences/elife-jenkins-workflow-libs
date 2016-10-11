def call(Closure preliminaryStep=null, marker=null, environmentName='end2end') {
    lock(environment) {
        if (environmentName == 'end2end') {
            builderStartAll(elifeEnd2endStacks())
        }
        if (preliminaryStep != null) {
            preliminaryStep()
        }

        def additionalArguments = ''
        if (marker) {
            additionalArguments = additionalArguments + "-m ${marker}"
        }

        sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}execute.sh ${additionalArguments} || echo TESTS FAILED"
        
        def testXmlArtifact = "${env.BUILD_TAG}.${environmentName}.junit.xml"
        sh "cp ${env.SPECTRUM_PREFIX}build/junit.xml ${testXmlArtifact}"
        builderTestArtifact testXmlArtifact

        def testLogArtifact = "${env.BUILD_TAG}.${environmentName}.log"
        sh "cp ${env.SPECTRUM_PREFIX}build/test.log ${testLogArtifact}"
        archive testLogArtifact

        elifeVerifyJunitXml testXmlArtifact
    }
}
