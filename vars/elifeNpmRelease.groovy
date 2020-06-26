def packageVersion() {
    // TODO: return null on empty or error
    return "cat package.json | jq .version"
}

def packageName() {
    // TODO: return null on empty or error
    return sh "cat package.json | jq .name"
}

def publishedVersions() {
    // TODO: return an empty list if nothing has been published
    // TODO: return null on error
    results = sh "npm show ${packageName()} version"
    return results.split()
}

def call() {
    // if there is no package name or version or there is an error fetching them, die loudly
    pkgver = packageVersion()
    assert pkgver : "'version' in package.json not found"
    
    pkgname = packageName()
    assert pkgname : "'name' in package.json not found"
   
    // if we're having problems talking to npm, die loudly
    published = publishedVersions()
    assert published : "error fetching list of published packages for '${pkgname}'"

    // if the package version is present in the list of published versions, do nothing
    if pkgver in published {
        println "Package '${pkgname}' has already published version '${pkgver}' on npm. Nothing to do."
        return
    }
    
    // state at this point: this version of this package does not exist on npm
    
    // TODO: where to pull token from?
    sh "npm publish --dry-run"
}
