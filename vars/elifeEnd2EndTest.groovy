def call() {
    node("end2end") {
        sh "cd ${env.SPECTRUM_PREFIX}; sudo -H -u elife ${env.SPECTRUM_PREFIX}execute.sh"
        step([$class: "JUnitResultArchiver", testResults: "${env.SPECTRUM_PREFIX}build/junit.xml"])
    }
}
