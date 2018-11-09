// see elifeSpectrum.txt
def call(Map parameters) {
    Map deploy = parameters.get('deploy', null)
    Closure preliminaryStep = {}
    Closure rollbackStep = {}
    if (deploy) {
        assert deploy.get('stackname') != null
        assert deploy.get('revision') != null
        assert deploy.get('folder') != null
        def concurrency = deploy.get('concurrency', 'serial')
        if (deploy.get('preliminaryStep')) {
            preliminaryStep = deploy.get('preliminaryStep')
        } else {
            preliminaryStep = {
                builderDeployRevision deploy.get('stackname'), deploy.get('revision'), concurrency
                builderSmokeTests deploy.get('stackname'), deploy.get('folder')
            }
        }
        if (deploy.get('rollbackStep')) {
            rollbackStep = deploy.get('rollbackStep')
        } else {
            rollbackStep = {
                builderDeployRevision deploy.get('stackname'), 'approved', concurrency
                builderSmokeTests deploy.get('stackname'), deploy.get('folder')
            }
        }
    }
    String marker = parameters.get('marker')
    String environmentName = parameters.get('environmentName', 'end2end')
    Integer processes = parameters.get('processes', 10)
    String revision = parameters.get('revision', 'master')
    String articleId = parameters.get('articleId')

    lock('spectrum') {
        lock(environmentName) {
            def stacks = ['elife-libraries--spectrum']
            if (environmentName == 'end2end') {
                stacks += elifeEnd2endStacks()
            }
            builderStartAll(stacks)

            try {
                preliminaryStep()

                def additionalFilteringArguments = ''
                if (marker) {
                    additionalFilteringArguments = additionalFilteringArguments + "-m ${marker} "
                }
                if (articleId) {
                    additionalFilteringArguments = additionalFilteringArguments + "--article-id=${articleId} "
                }

                elifeOnNode({
                    sh "cd ${env.SPECTRUM_PREFIX}; ${env.SPECTRUM_PREFIX}checkout.sh ${revision}"
                    if (!additionalFilteringArguments) {
                        // before starting the whole suite, run simple smoke test first
                        sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environmentName} SPECTRUM_TIMEOUT=120 ${env.SPECTRUM_PREFIX}execute-simplest-possible-test.sh"
                    }
                    sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environmentName} SPECTRUM_PROCESSES=${processes} ${env.SPECTRUM_PREFIX}execute.sh ${additionalFilteringArguments}"
                }, 'elife-libraries--spectrum')
            } catch (e) {
                echo "Failure while running spectrum tests: ${e.message}"
                echo "Attempting to rollback (if the project specifies it) before terminating the build with an error"
                rollbackStep()
                echo "Rollback successful"
                throw e
            } finally {
                
                elifeOnNode({
                    def testXmlArtifact = "${env.BUILD_TAG}.${environmentName}.junit.xml"
                    if (fileExists("${env.SPECTRUM_PREFIX}build/junit.xml")) {
                        sh "cp ${env.SPECTRUM_PREFIX}build/junit.xml ${testXmlArtifact}"
                        echo "Found: ${testXmlArtifact}"
                        step([$class: "JUnitResultArchiver", testResults: testXmlArtifact])
                    }

                    if (fileExists("${env.SPECTRUM_PREFIX}build/test.log")) {
                        def testLogArtifact = "${env.BUILD_TAG}.${environmentName}.log"
                        sh "cp ${env.SPECTRUM_PREFIX}build/test.log ${testLogArtifact}"
                        archive testLogArtifact
                    }
                    
                    sh "cp ${env.SPECTRUM_PREFIX}build/screenshots/*.png . || true"
                    archiveArtifacts artifacts: '*.png', allowEmptyArchive: true

                    sh "cd ${env.SPECTRUM_PREFIX}; ./reset-build.sh"

                    elifeVerifyJunitXml testXmlArtifact
                }, 'elife-libraries--spectrum')
            }
        }
    }
}
