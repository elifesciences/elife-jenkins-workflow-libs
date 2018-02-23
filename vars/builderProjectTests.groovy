def retrieveArtifacts(stackname, testArtifacts) {
    echo "Looking for test artifacts: ${testArtifacts}"
    for (int i = 0; i < testArtifacts.size(); i++) {
        builderTestArtifact testArtifacts.get(i), stackname
    }
}

def defineProjectTests(stackname, folder) {
    def actions = [:]
    def projectTestsParallelScripts = findFiles(glob: '.ci/*')
    for (int i = 0; i < projectTestsParallelScripts.size(); i++) {
        def projectTestsParallelScript = "cd ${folder}; ${projectTestsParallelScripts[i].path}"
        actions[projectTestsParallelScripts[i].name] = {
            builderCmd stackname, projectTestsParallelScript
        }
    }
    if (fileExists('project_tests.sh')) {
        def projectTestsCmd = "cd ${folder}; ./project_tests.sh"
        actions['project_tests.sh'] = {
            builderCmd stackname, projectTestsCmd
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
                actions = defineProjectTests stackname, folder
                parallel actions
            } finally {
                retrieveArtifacts stackname, testArtifacts
            }
        } else {
            error("You requested to run '${order.get(i)}' tests, but the only allowed values are 'smoke' and 'project'.");
        }
    }
}
