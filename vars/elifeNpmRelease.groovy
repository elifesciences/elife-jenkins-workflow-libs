def command(cmd) {
    sh(script: cmd, returnStatus: true)
}

def command_wstdout(cmd) {
    sh(script: cmd, returnStdout: true).trim()
}

def packageAttr(attr) {
    try {
        command_wstdout "cat package.json | jq -r .${attr}"
    } catch (Exception e) {
        println "Failed to find '${attr}' in package.json"
    }
}

def packageVersion() { packageAttr "version" }

def packageName() { packageAttr "name" }

def publishedVersions(pkgname) {
    retval = command "npm view \"${pkgname}\" version --loglevel silent"
    if (retval != 0) {
        println "Problem fetching releases for ${pkgname} (package may not exist)"
        return []
    }
    try {
        // now that we know versions exist
        // run command *again* to capture the list of versions on stdout
        results = command_wstdout "npm view \"${pkgname}\" version"
        return results.split()
    } catch (Exception e) {
        println "Error listing npm packages for ${pkgname}"
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
        println "Package '${pkgname}' has already published version '${pkgver}' on npm. Nothing to do."
        return
    }

    retval = sh(script: "npm install", returnStatus: true)
    assert retval == 0 : "failed to build package"
    
    // state at this point: this version of this package does not exist on npm
    
    withCredentials([string(credentialsId: 'npm-credentials', variable: 'NPM_TOKEN')]) {
        command "echo \"//registry.npmjs.org/:_authToken=\${NPM_TOKEN}\" > .npmrc"
        command "npm publish --access public"
        assert retval == 0 : "failed to publish package '${pkgname}'"
    }
}
