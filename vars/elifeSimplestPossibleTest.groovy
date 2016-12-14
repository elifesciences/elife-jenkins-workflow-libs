def call(environment='continuumtest') {
    lock(environment) {
        sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environment} sudo -H -u elife ${env.SPECTRUM_PREFIX}execute-simplest-possible-test.sh"
    }
}
