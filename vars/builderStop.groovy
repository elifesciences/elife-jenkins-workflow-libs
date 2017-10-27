def call(stackname, resources=[]) {
    def String resourcesOptions = ''
    if (resources) {
        resourcesOptions = ',' + resources.join(',')
    }
    sh "${env.BUILDER_PATH}bldr 'stop:${stackname}${resourcesOptions}'"
}
