// TODO: reorder this horrible list of parameters
// better: http://docs.groovy-lang.org/latest/html/documentation/#_named_arguments
def call(Closure preliminaryStep=null, marker=null, environmentName='end2end', processes=10, revision='master', articleId=null) {
    elifeSpectrum(preliminaryStep: preliminaryStep, marker: marker, environmentName: environmentName, processes: processes, revision: revision, articleId: articleId)
}
