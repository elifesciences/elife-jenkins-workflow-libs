def retrieveArtifacts(stackname, testArtifacts, folder) {
    echo "Looking for test artifacts: ${testArtifacts}"
    for (int i = 0; i < testArtifacts.size(); i++) {
        def testArtifact = testArtifacts.get(i)
        if (testArtifact[0..0] != '/') {
            testArtifact = "${folder}/${testArtifact}"
        }
        builderTestArtifact(new RemoteTestArtifact(testArtifact), stackname)
    }
}

def defineProjectTests(stackname, folder) {
    def String commit = elifeGitRevision()
    def actions = [:]
    def projectTestsParallelScripts = findFiles(glob: '.ci/*')
    for (int i = 0; i < projectTestsParallelScripts.size(); i++) {
        def projectTestsParallelScript = "cd ${folder}; ${projectTestsParallelScripts[i].path}"
        def name = "${projectTestsParallelScripts[i].name}"
        actions[name] = {
            withCommitStatus({
                builderCmd stackname, projectTestsParallelScript
            }, name, commit)
        }
    }
    if (fileExists('project_tests.sh')) {
        def projectTestsCmd = "cd ${folder}; ./project_tests.sh"
        actions['project_tests.sh'] = {
            withCommitStatus({
                builderCmd stackname, projectTestsCmd
            }, 'project_tests.sh', commit)
        }
    }

    if (!actions) {
        throw new Exception("No .ci/ or project_tests.sh script was found")
    }

    return actions
}

def call(stackname, folder, testArtifacts=[], order=['project', 'smoke']) {
    for (int i = 0; i < order.size(); i++) {
        if (order.get(i) == 'smoke') {
            builderSmokeTests stackname, folder
        } else if (order.get(i) == 'project') {
            try {
                def allArtifacts = testArtifacts.join(' ')
                builderCmd stackname, "cd ${folder}; rm -rf ${allArtifacts}"
                actions = defineProjectTests stackname, folder
                parallel actions
            } finally {
                retrieveArtifacts stackname, testArtifacts, folder
            }
        } else {
            error("You requested to run '${order.get(i)}' tests, but the only allowed values are 'smoke' and 'project'.");
        }
    }
}
