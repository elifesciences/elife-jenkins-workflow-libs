def defineLocalTests() {
    def String commit = elifeGitRevision()
    def actions = [:]
    def projectTestsParallelScripts = findFiles(glob: '.ci/*')
    for (int i = 0; i < projectTestsParallelScripts.size(); i++) {
        def name = "${projectTestsParallelScripts[i].name}"
        actions[name] = {
            withCommitStatus({
                sh "${projectTestsParallelScripts[i].path}"
            }, name, commit)
        }
    }
    if (fileExists('project_tests.sh')) {
        actions['project_tests.sh'] = {
            withCommitStatus({
                sh 'project_tests.sh'
            }, 'project_tests', commit)
        }
    }

    if (!actions) {
        throw new Exception("No .ci/ or project_tests.sh script was found")
    }

    return actions
}

def call(cmd, testArtifacts=[]) {
    actions = defineLocalTests()
    try {
        parallel actions
    } finally {
        for (int i = 0; i < testArtifacts.size(); i++) {
            def localTestArtifact = testArtifacts.get(i)
            if (!readFile(localTestArtifact)) {
                echo "Artifact ${localTestArtifact} not found"
                continue
            }
            echo "Found ${localTestArtifact}"
            step([$class: "JUnitResultArchiver", testResults: localTestArtifact])
        }
    }
}
