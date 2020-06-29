def packageVersion() {
    try {
        return sh(script: "cat package.json | jq .version", returnStdout: true).trim()
    } catch (Exception e) {
        echo "Failed to find 'version' in package.json"
        return null
    }
}

def packageName() {
    try {
        return sh(script: "cat package.json | jq .name", returnStdout: true).trim()
    } catch (Exception e) {
        echo "Failed to find 'name' in package.json"
        return null
    }
}

def publishedVersions(pkgname) {
    // do I need a try+catch when I'm capturing the retval?
    retval = sh(script: "npm view ${pkgname} version", returnStatus=true).trim()
    if (retval == 1) {
        // package doesn't exist/nothing has been published
        return []
    }
    else if (retval > 1) {
        // unhandled error talking to npm
        echo "Error attempting to list npm packages for ${pkgname}"
        return null
    }
    try {
        // now that we know versions exist
        // run command *again* to capture the list of versions on stdout
        // todo: can I captured stdout + retval in one go??
        results = sh(script: "npm view ${pkgname} version", returnStdout: true).trim()
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
    assert published : "error fetching list of published packages for '${pkgname}'"

    // if the package version is present in the list of published versions, do nothing
    if (published.containsKey(pkgver)) {
        println "Package '${pkgname}' has already published version '${pkgver}' on npm. Nothing to do."
        return
    }
    
    // state at this point: this version of this package does not exist on npm
    
    // TODO: where to pull token from?
    sh "npm publish --dry-run"
}
