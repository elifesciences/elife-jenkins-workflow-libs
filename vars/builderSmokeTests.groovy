def call(stackname, folder) {
    def smokeTestsCmd = "cd ${folder}; if [ -e smoke_tests.sh ]; then ./smoke_tests.sh; fi"
    builderCmd stackname, smokeTestsCmd
}
