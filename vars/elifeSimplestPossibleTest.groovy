def call(environment='continuumtest') {
    lock('spectrum') {
        lock(environment) {
            if (environment == 'end2end') {
                builderStartAll(elifeEnd2endStacks())
            }
            sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}checkout.sh origin/master"
            sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environment} sudo -H -u elife ${env.SPECTRUM_PREFIX}execute-simplest-possible-test.sh"
        }
    }
}
