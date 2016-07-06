def call(project, environment) {
    elifeSlack "Deploying *${project}* on *${environment}* (<${env.BUILD_URL}|Build>)"
}
