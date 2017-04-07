def call(Map parameters) {
    assert deploy.get('pipeline') != null
    assert deploy.get('type') != null

    def pipeline = deploy.get('pipeline')
    def type = deploy.get('type')
    def number = deploy.get('number')
    def commit = deploy.get('commit')

    def additionalArguments = ''
    if (number) {
        additionalArguments = additionalArguments + ' --number ${number}"
    }
    if (commit) {
        additionalArguments = additionalArguments + ' --commit ${commit}"
    }

    def directory = "/var/lib/jenkins/statistics"
    def code = sh script: "/usr/local/jenkins-scripts/pipelineevent.py --directory ${directory} --pipeline ${pipeline} --type ${type}${additionalArguments}", returnStatus: true
    if (code) {
        echo "Failed to record pipeline event; status code: ${code}"
        currentBuild.result = 'UNSTABLE'
    }
}
