def call(environment='continuumtest') {
    lock('spectrum') {
        lock(environment) {
            if (environment == 'end2end') {
                builderStartAll(elifeEnd2endStacks())
            }
            elifeOnNode({
                sh "cd ${env.SPECTRUM_PREFIX}; ${env.SPECTRUM_PREFIX}checkout.sh origin/master"
                // before starting the whole suite, run simple smoke test first
                sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environmentName} SPECTRUM_TIMEOUT=120 ${env.SPECTRUM_PREFIX}execute-simplest-possible-test.sh"
            }, 'elife-libraries--spectrum')
        }
    }
}
