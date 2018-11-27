// see elifeSpectrum.txt
def call(Map parameters) {
    Map deploy = parameters.get('deploy', null)
    Closure preliminaryStep = {}
    Closure rollbackStep = {}
    def String projectRevision = null
    if (deploy) {
        assert deploy.get('stackname') != null
        assert deploy.get('revision') != null
        projectRevision = deploy.get('revision')
        assert deploy.get('folder') != null
        def concurrency = deploy.get('concurrency', 'serial')
        if (deploy.get('preliminaryStep')) {
            preliminaryStep = deploy.get('preliminaryStep')
        } else {
            preliminaryStep = {
                builderDeployRevision deploy.get('stackname'), projectRevision, concurrency
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
    String spectrumRevision = parameters.get('revision', 'master')
    String commitStatusRevision = null
    if (parameters.get('revision')) {
        // elife-spectrum run
        commitStatusRevision = parameters.get('revision')
    } else if (projectRevision) {
        // project test-* pipeline run
        commitStatusRevision = projectRevision
    } else {
        commitStatusRevision = 'master'
    }
    String articleId = parameters.get('articleId')

    lock('spectrum') {
        lock(environmentName) {
            def stacks = ['elife-libraries--spectrum']
            if (environmentName == 'end2end') {
                stacks += elifeEnd2endStacks()
            }
            builderStartAll(stacks)

            try {
                withCommitStatus({
                    preliminaryStep()
                }, "end2end/deploy", commitStatusRevision)

                def additionalFilteringArguments = ''
                if (marker) {
                    additionalFilteringArguments = additionalFilteringArguments + "-m ${marker} "
                }
                if (articleId) {
                    additionalFilteringArguments = additionalFilteringArguments + "--article-id=${articleId} "
                }

                withCommitStatus({
                    elifeOnNode({
                        sh "cd ${env.SPECTRUM_PREFIX}; ${env.SPECTRUM_PREFIX}checkout.sh ${spectrumRevision}"
                        if (!additionalFilteringArguments) {
                            // before starting the whole suite, run simple smoke test first
                            sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environmentName} SPECTRUM_TIMEOUT=120 ${env.SPECTRUM_PREFIX}execute-simplest-possible-test.sh"
                        }
                        sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environmentName} SPECTRUM_PROCESSES=${processes} ${env.SPECTRUM_PREFIX}execute.sh ${additionalFilteringArguments}"
                    }, 'elife-libraries--spectrum')
                }, "end2end/test", commitStatusRevision)
            } catch (e) {
                withCommitStatus({
                    echo "Failure while running spectrum tests: ${e.message}"
                    echo "Attempting to rollback (if the project specifies it) before terminating the build with an error"
                        preliminaryStep()
                    rollbackStep()
                    echo "Rollback successful"
                }, "end2end/rollback", commitStatusRevision)
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
