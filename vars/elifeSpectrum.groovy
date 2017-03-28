// see elifeSpectrum.txt
def call(Map parameters) {
    Map deploy = parameters.get('deploy', null)
    Closure preliminaryStep = {}
    Closure rollbackStep = {}
    if (deploy) {
        assert deploy.get('stackname') != null
        assert deploy.get('revision') != null
        assert deploy.get('folder') != null
        preliminaryStep = {
            builderDeployRevision deploy.get('stackname'), deploy.get('revision')
            builderSmokeTests deploy.get('stackname'), deploy.get('folder')
        }
        rollbackStep = {
            builderDeployRevision deploy.get('stackname'), 'approved'
            builderSmokeTests deploy.get('stackname'), deploy.get('folder')
        }
    }
    if (parameters.get('preliminaryStep')) {
        preliminaryStep = parameters.get('preliminaryStep')
    }
    if (parameters.get('rollbackStep')) {
        rollbackStep = parameters.get('rollbackStep')
    }
    String marker = parameters.get('marker')
    String environmentName = parameters.get('environmentName', 'end2end')
    Integer processes = parameters.get('processes', 10)
    String revision = parameters.get('revision', 'master')
    String articleId = parameters.get('articleId')

    lock('spectrum') {
        lock(environmentName) {
            if (environmentName == 'end2end') {
                builderStartAll(elifeEnd2endStacks())
            }
            preliminaryStep()

            try {
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
            } catch (e) {
                echo "Failure while running spectrum tests: ${e.message}"
                echo "Attempting to rollback (if the project specifies it) before terminating the build with an error"
                rollbackStep()
                throw e
            }
        }
    }
}
