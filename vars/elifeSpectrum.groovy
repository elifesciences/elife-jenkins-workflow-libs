// see elifeSpectrum.txt
def call(Map parameters) {
    Closure preliminaryStep = parameters.get('preliminaryStep', {})
    Closure rollbackStep = parameters.get('rollbackStep', {})
    String marker = parameters.get('marker')
    String environmentName = parameters.get('environment', 'end2end')
    Integer processes = parameters.get('processes', 10)
    String revision = parameters.get('revision', 'master')
    String articleId = parameters.get('articleId')

    lock(environmentName) {
        if (environmentName == 'end2end') {
            builderStartAll(elifeEnd2endStacks())
        }
        preliminaryStep()

        def additionalFilteringArguments = ''
        if (marker) {
            additionalFilteringArguments = additionalFilteringArguments + "-m ${marker} "
        }
        if (articleId) {
            additionalFilteringArguments = additionalFilteringArguments + "--article-id=${articleId} "
        }

        sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}checkout.sh ${revision}"
        if (!additionalFilteringArguments) {
            // before starting the whole suite, run simple smoke test first
            sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environmentName} SPECTRUM_TIMEOUT=120 sudo -H -u elife ${env.SPECTRUM_PREFIX}execute-simplest-possible-test.sh"
        }
        sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environmentName} SPECTRUM_PROCESSES=${processes} sudo -H -u elife ${env.SPECTRUM_PREFIX}execute.sh ${additionalFilteringArguments}|| echo TESTS FAILED"
        
        def testXmlArtifact = "${env.BUILD_TAG}.${environmentName}.junit.xml"
        sh "cp ${env.SPECTRUM_PREFIX}build/junit.xml ${testXmlArtifact}"
        builderTestArtifact testXmlArtifact

        def testLogArtifact = "${env.BUILD_TAG}.${environmentName}.log"
        sh "cp ${env.SPECTRUM_PREFIX}build/test.log ${testLogArtifact}"
        archive testLogArtifact

        elifeVerifyJunitXml testXmlArtifact
    }
}
