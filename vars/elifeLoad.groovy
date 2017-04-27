// see elifeLoad.txt
def call(Map parameters) {
    String environmentName = parameters.get('environmentName', 'end2end')
    String revision = parameters.get('revision', 'master')

    lock('spectrum') {
        lock(environmentName) {
            if (environmentName == 'end2end') {
                builderStartAll(elifeEnd2endStacks())
            }

            sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}checkout.sh ${revision}"
            sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environmentName} sudo -H -u elife ${env.SPECTRUM_PREFIX}load-small.sh"
        }
    }
}
