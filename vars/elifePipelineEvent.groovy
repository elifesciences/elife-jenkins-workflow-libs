def call(Map parameters) {
    assert parameters.get('pipeline') != null
    assert parameters.get('type') != null

    def pipeline = parameters.get('pipeline').replaceAll("/", "--")
    def type = parameters.get('type')
    def number = parameters.get('number')
    def commit = parameters.get('commit')

    def additionalArguments = ''
    if (number) {
        additionalArguments = additionalArguments + " --number ${number}"
    }
    if (commit) {
        additionalArguments = additionalArguments + " --commit ${commit}"
    }

    def directory = '/var/lib/jenkins/statistics'
    def code = sh script: "/usr/local/jenkins-scripts/pipelineevent.py --directory ${directory} --pipeline ${pipeline} --type ${type}${additionalArguments}", returnStatus: true
    if (code) {
        echo "Failed to record pipeline event; status code: ${code}"
        currentBuild.result = 'UNSTABLE'
    }
}
