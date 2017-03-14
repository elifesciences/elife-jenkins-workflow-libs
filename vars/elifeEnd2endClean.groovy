def call() {
    lock('end2end') {
        sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}checkout.sh master"
        sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}clean.sh"
    }
}
