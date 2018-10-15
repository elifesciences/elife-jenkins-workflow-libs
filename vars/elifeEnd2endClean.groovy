def call() {
    lock('spectrum') {
        elifeOnNode({
            sh "cd ${env.SPECTRUM_PREFIX}; ./checkout.sh ${revision}"
            sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=end2end ./clean.sh"
        }, 'elife-libraries--spectrum')
    }
}
