def packageVersion() {
    try {
        return sh(script: "cat package.json | jq -r .version ", returnStdout: true).trim()
    } catch (Exception e) {
        echo "Failed to find 'version' in package.json"
        return null
    }
}

def packageName() {
    try {
        return sh(script: "cat package.json | jq -r .name", returnStdout: true).trim()
    } catch (Exception e) {
        echo "Failed to find 'name' in package.json"
        return null
    }
}

def publishedVersions(pkgname) {
    retval = sh(script: "npm view \"${pkgname}\" version --loglevel silent", returnStatus: true)
    if (retval != 0) {
	echo "Problem fetching releases for ${pkgname} (package may not exist)"
        return []
    }
    try {
        // now that we know versions exist
        // run command *again* to capture the list of versions on stdout
        // TODO: can I capture stdout + retval in one go??
        results = sh(script: "npm view \"${pkgname}\" version", returnStdout: true).trim()
        return results.split()
    } catch (Exception e) {
        echo "Error listing npm packages for ${pkgname}"
        return null
    }
}

def call() {
    // if there is no package name or version or there is an error fetching them, die loudly
    pkgver = packageVersion()
    assert pkgver : "'version' in package.json not found"
    
    pkgname = packageName()
    assert pkgname : "'name' in package.json not found"
   
    // if we're having problems talking to npm, die loudly
    published = publishedVersions(pkgname)
    assert published != null : "error fetching releases for '${pkgname}'"

    // if the package version is present in the list of published versions, do nothing
    if (published.contains(pkgver)) {
        echo "Package '${pkgname}' has already published version '${pkgver}' on npm. Nothing to do."
        return
    }

    retval = sh(script: "npm install", returnStatus: true)
    assert retval == 0 : "failed to build package"
    
    // state at this point: this version of this package does not exist on npm
    
    withCredentials([string(credentialsId: 'npm-credentials', variable: 'NPM_TOKEN')]) {
        sh "echo \"//registry.npmjs.org/:_authToken=\${NPM_TOKEN}\" > .npmrc"
        retval = sh(script: "npm publish --access public --dry-run", returnStatus: true)
        assert retval == 0 : "failed to publish package '${pkgname}'"
    }
}
