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

def builderCmdAdapter(stackname, command, label) {
    echo "builderCmdAdapter: ${stackname}, ${command}, ${label}"
    builderCmd stackname, command
}

def call(stackname, folder, testArtifacts=[], order=['project', 'smoke']) {
    for (int i = 0; i < order.size(); i++) {
        if (order.get(i) == 'smoke') {
            builderSmokeTests stackname, folder
        } else if (order.get(i) == 'project') {
            try {
                def allArtifacts = testArtifacts.join(' ')
                builderCmd stackname, "cd ${folder}; rm -rf ${allArtifacts}"
                actions = _defineProjectTests(stackname, folder, builderCmdAdapter)
                echo "Actions created"
                actions.each({ n, v -> 
                    echo "Name of action: ${n}"
                })
                echo "Executing actions"
                parallel actions
            } finally {
                retrieveArtifacts stackname, testArtifacts, folder
            }
        } else {
            error("You requested to run '${order.get(i)}' tests, but the only allowed values are 'smoke' and 'project'.");
        }
    }
}
