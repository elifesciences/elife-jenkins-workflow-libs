/* Don't freak out, what is happening in this file is actually very simple.

The bash script to be run couldn't be loaded from the 'resources' directory (for some reason) so I've embedded it here as
a simple string. No variable interpolation, just a long string.

The string is written to a file within the workspace called 'pypi-release.sh', made executable and then executed with
either 'test' or 'live' as the first parameter, depending on the target host.

*/

def command(cmd) {
    sh(script: cmd, returnStatus: true)
}

/** returns a bash script that is written to the workspace and executed **/
def script () {
    // doesn't work :(
    //releaseScriptString = libraryResource 'pypi-release.sh' 
    //writeFile file: 'pypi-release.sh', text: releaseScriptString
    // if anyone can figure out a way of reading this from the 'resources/' directory where it belongs, let me know
    
    return '''#!/bin/bash
# usage: `./release.sh [<test|live>]`
# calls `setup.py` to build Python distributables and uploads the result to `test.pypi.org` or `pypi.org`
set -eu

index=${1:-"test"}

pypi_repository="testpypi"
pypi_url="https://test.pypi.org/pypi"
if [ "$index" = "live" ]; then
    pypi_repository="pypi"
    pypi_url="https://pypi.org/pypi"
fi

# avoid setting this on the command line, it will be visible in your history.
token="$TWINE_PASSWORD"
if [ -z "$token" ]; then
    echo "TWINE_PASSWORD is not set. This is the pypi.org or test.pypi.org API token"
    exit 1
fi

echo "--- building"
rm -rf ./release-venv/ dist/ build/ *.egg-info
python3 -m venv release-venv
source release-venv/bin/activate
python3 -m pip install --upgrade pip setuptools wheel twine
python3 setup.py sdist bdist_wheel

echo "--- testing build"
python3 -m twine check \
    --strict \
    dist/*

echo "--- checking against remote release"
local_version=$(python3 setup.py --version)
local_version="($local_version)" # hack

package_name=$(python3 setup.py --name)
# "elife-dummy-python-release-project (0.0.1)                      - A small example package"  =>  "(0.0.1)"
remote_version=$(pip search "$package_name" --index "$pypi_url" --isolated | grep "$package_name" | awk '{ print $2 }')
if [ "$local_version" = "$remote_version" ]; then
    echo "Local version '$local_version' is the same as the remote version '$remote_version'. Not releasing."
    exit 0 # not a failure case
else
    echo "Local version '$local_version' not found remotely, releasing."
fi

echo "--- uploading"
python3 -m twine upload \
    --repository "$pypi_repository" \
    --username "__token__" \
    --password "$token" \
    dist/*
'''
}

def writeScript(pathToResource) {
    // write string to local file
    releaseScript = new File(pwd() + "/" + pathToResource)
    releaseScript.setText(script())
    command "chmod +x ${releaseScript.name}" // make local file executable
    
    // "/var/lib/jenkins/workspace/release/dummy-python-release-project/pypi-release.sh"
    return releaseScript.absolutePath 
}

def call(index='live') {
    assert (index == 'test' || index == 'live'): "pypi index must be either 'test' or 'live'"
    writeScript "pypi-release.sh"
    withCredentials([string(credentialsId: "pypi-credentials--${index}", variable: 'TWINE_PASSWORD')]) {
        retval = command "./pypi-release.sh --index ${index}"
        assert retval == 0 : "failed to publish package"
    }
}
