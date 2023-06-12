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
    // lsh@2023-06-09: docs are here https://www.jenkins.io/doc/book/pipeline/shared-libraries/#loading-resources
    // my future self is curious about what wasn't working...

    return '''#!/bin/bash
# usage: `./release.sh [<test|live>]`
# calls `setup.py` to build Python distributables and uploads the result to `test.pypi.org` or `pypi.org`
set -exuo pipefail

index=${1:-"test"}

pypi_repository="testpypi"
if [ "$index" == "live" ]; then
    pypi_repository="pypi"
fi

# avoid setting this on the command line, it will be visible in your history.
token="$TWINE_PASSWORD"
if [ -z "$token" ]; then
    echo "TWINE_PASSWORD is not set. This is the pypi.org or test.pypi.org API token"
    exit 1
fi

echo "--- building"
rm -rf ./release-venv/ dist/ build/ ./*.egg-info
python3 -m venv release-venv
# shellcheck disable=SC1091
source release-venv/bin/activate
# needed later to execute 'activate' as 'source' not available
chmod +x release-venv/bin/activate
python3 -m pip install --upgrade pip wheel
# twine has a transitive dependency on cryptography that requires pip upgraded *first* else it attempts to build it
# using the rust programming language.
python3 -m pip install --upgrade setuptools twine
python3 setup.py sdist bdist_wheel

echo "--- testing build"
python3 -m twine check \
    --strict \
    dist/*

# lsh@2021-01-26: pypi disabled the 'search' service on its live server with no intent to turn it back on.
# I can't test for the already-released version so we just have to push the package and see if it gets rejected.

local_version=$(python3 setup.py --version)

echo "--- uploading"
set +e
python3 -m twine upload \
    --repository "$pypi_repository" \
    --username "__token__" \
    --password "$token" \
    dist/* > release-output.txt 2>&1
rc=$?
set -e

cat release-output.txt

if [ "$rc" == "0" ]; then
    # successful release!
    exit 0
elif grep "File already exists." release-output.txt --silent; then
    echo "Local version '$local_version' is the same as the remote version. Not releasing."
    exit 0 # not a failure case
else
    exit 1
fi
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
        retval = command "./pypi-release.sh ${index}"
        assert retval == 0 : "failed to publish package"
        // the "| tr ..." is to trim the new line from the version so the param doesn't carry it around downstream.
        return sh(script:"./release-venv/bin/activate && python3 setup.py --version | tr --delete '\n'", returnStdout:true)
    }
}
