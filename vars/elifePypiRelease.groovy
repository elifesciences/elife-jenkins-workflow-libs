def command(cmd) {
    sh(script: cmd, returnStatus: true)
}

/*
def command_wstdout(cmd) {
    sh(script: cmd, returnStdout: true).trim()
} 
*/

def call(index='test') {
    assert (index == 'test' || index == 'live'): "pypi index must be either 'test' or 'live'"
    
    withCredentials([string(credentialsId: 'pypi-credentials', variable: 'TWINE_PASSWORD')]) {
    
        retval = command "/path/to/release.sh --index ${index}"
        assert retval == 0 : "failed to publish package '${pkgname}'"
    }
}
