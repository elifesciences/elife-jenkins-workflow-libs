def call(cmd, testArtifacts=[]) {
    try {
        sh cmd
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
