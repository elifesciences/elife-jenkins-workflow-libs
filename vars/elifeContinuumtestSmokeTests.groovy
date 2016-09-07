def call() {
    sh "export SPECTRUM_ENVIRONMENT=continuumtest; sudo -H -u elife /srv/elife-spectrum/execute-simplest-possible-test.sh"
}
