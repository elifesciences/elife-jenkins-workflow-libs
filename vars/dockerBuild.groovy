def call(project, tag='latest', organization='elifesciences') {
    sh "docker build -t ${organization}/${project}:${tag} ."
}
