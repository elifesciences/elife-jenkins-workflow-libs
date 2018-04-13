def defineLocalTests(projectTestsCommand=null) {
    def String commit = elifeGitRevision()
    def actions = [:]
    //def projectTestsParallelScripts = findFiles(glob: '.ci/*')
    //for (int i = 0; i < projectTestsParallelScripts.size(); i++) {
    //    def name = "${projectTestsParallelScripts[i].name}"
    //    def path = "${projectTestsParallelScripts[i].path}"
    //    actions[name] = {
    //        withCommitStatus({
    //            sh path
    //        }, name, commit)
    //    }
    //}
    if (projectTestsCommand) {
        actions['project_tests.sh'] = {
            withCommitStatus({
                sh projectTestsCommand
            }, 'project_tests', commit)
        }
    }

    if (!actions) {
        throw new Exception("No .ci/ or project_tests.sh script was found")
    }

    return actions
}

def call(cmd, testArtifacts=[]) {
    actions = defineLocalTests(cmd)
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
