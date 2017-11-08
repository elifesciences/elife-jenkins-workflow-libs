def call(project, environment) {
    elifeSlack "Deploying *${project}* on *${environment}* (<${env.RUN_DISPLAY_URL}|Build>, <${env.RUN_CHANGES_DISPLAY_URL}|Changes>)"
}
