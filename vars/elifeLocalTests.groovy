def call(cmd, testArtifacts=[]) {
    try {
        sh cmd
    } finally {
        for (int i = 0; i < testArtifacts.size(); i++) {
            def localTestArtifact = testArtifacts.get(i)
            builderTestArtifact localTestArtifact, null, null, true
        }
    }
}
