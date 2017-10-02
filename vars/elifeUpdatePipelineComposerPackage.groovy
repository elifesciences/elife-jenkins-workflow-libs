// see elifeUpdatePipelineComposerPackage.txt
def call(Map parameters) {
    assert parameters.get('stackname') != null
    assert parameters.get('folder') != null
    assert parameters.get('hostname') != null
    assert parameters.get('packageName') != null
    def String stackname = parameters.get('stackname') 
    // ensures `folder` ends with `/`
    def String folder = ensureFinalSlash(parameters.get('folder'))
    def String hostname = parameters.get('hostname')
    def String packageName = parameters.get('packageName')
    def String basePackageName = packageName.split("/")[1]
    def String branchPrefix = basePackageName.replaceAll("[^a-z0-9]", "_")
    def String additionalPackageNames = parameters.get('additionalPackageNames', []).join(" ")
    def String currentVersion = {
        def version = sh(script: "jq '.packages[] | select(.name==\"${packageName}\").dist.reference' < composer.lock", returnStdout: true)
        echo "Version of {$packageName}: ${version}"
        return version
    }

    elifeUpdatePipeline(
        { commit ->
            lock(stackname) {
                def String originalVersion = currentVersion()
                builderDeployRevision stackname, commit
                builderCmd stackname, "composer update ${additionalPackageNames} ${packageName} --with-dependencies --no-suggest --no-interaction", folder
                builderSync hostname, folder
                def String newVersion = currentVersion()
                if (newVersion != originalVersion) {
                    sh "git add composer.lock"
                }
            }
        },
        {
            def subrepositorySummary = elifeGitSubrepositorySummary "vendor/${packageName}"
            return "Updated ${packageName} to ${subrepositorySummary}"
        },
        "update_${branchPrefix}/"
    )
}

def ensureFinalSlash(String folder) {
    return folder - ~/\/$/ + "/"
}
