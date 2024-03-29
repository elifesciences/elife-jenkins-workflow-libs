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
    def String spectrumRevision = parameters.get('revision')
    if (!spectrumRevision) {
        spectrumRevision = 'master'
    }
    def Map commitStatus = parameters.get('commitStatus', [:])
    if (commitStatus.get('repository')) {
        assert commitStatus.get('revision') != null
    }
    String commitStatusRepository = null
    String commitStatusRevision = null
    if (parameters.get('revision')) {
        // elife-spectrum run
        commitStatusRepository = 'elifesciences/elife-spectrum'
        commitStatusRevision = parameters.get('revision')
    } else if (commitStatus.get('repository')) {
        // elife-xpub-deployment run, pushing commit status to elife-xpub
        commitStatusRepository = commitStatus.get('repository')
        commitStatusRevision = commitStatus.get('revision')
    } else if (projectRevision) {
        // project test-* pipeline run, pushing commit status to itself
        commitStatusRevision = projectRevision
        commitStatusRepository = null
    } else {
        // should probably never happen
        commitStatusRepository = 'elifesciences/elife-spectrum'
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
                preliminaryStep()

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
                }, "${environmentName}/test", commitStatusRevision, commitStatusRepository)
            } catch (e) {
                withCommitStatus({
                    echo "Failure while running spectrum tests: ${e.message}"
                    echo "Attempting to rollback (if the project specifies it) before terminating the build with an error"
                    rollbackStep()
                    echo "Rollback successful"
                }, "${environmentName}/rollback", commitStatusRevision, commitStatusRepository)
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
                        archiveArtifacts artifacts: testLogArtifact, allowEmptyArchive: true
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
