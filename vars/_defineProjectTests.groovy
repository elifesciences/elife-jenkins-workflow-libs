def call(stackname, folder, callable) {
    def String commit = elifeGitRevision()
    def actions = [:]
    def projectTestsParallelScripts = findFiles(glob: '.ci/*')
    for (int i = 0; i < projectTestsParallelScripts.size(); i++) {
        def projectTestsParallelScript = "${projectTestsParallelScripts[i].path}"
        def name = "${projectTestsParallelScripts[i].name}"
        actions[name] = {
            withCommitStatus({
                callable stackname, projectTestsParallelScript, folder, name.toString()
            }, name, commit)
        }
    }
    if (fileExists('project_tests.sh')) {
        def projectTestsCmd = "./project_tests.sh"
        actions['project_tests.sh'] = {
            withCommitStatus({
                callable stackname, projectTestsCmd, folder, 'project_tests.sh'
            }, 'project_tests', commit)
        }
    }

    if (!actions) {
        throw new Exception("No .ci/ or project_tests.sh script was found")
    }

    return actions
}
