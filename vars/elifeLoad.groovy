// see elifeLoad.txt
def call(Map parameters) {
    String environmentName = parameters.get('environmentName', 'end2end')
    String revision = parameters.get('revision', 'master')

    lock('spectrum') {
        lock(environmentName) {
            def stacks = ['elife-libraries--spectrum']
            if (environmentName == 'end2end') {
                stacks += elifeEnd2endStacks()
            }
            builderStartAll(stacks)

            elifeOnNode({
                sh "cd ${env.SPECTRUM_PREFIX}; ./checkout.sh ${revision}"
                sh "cd ${env.SPECTRUM_PREFIX}; SPECTRUM_ENVIRONMENT=${environmentName} ./load-small.sh"
            }, 'elife-libraries--spectrum')
        }
    }
}
