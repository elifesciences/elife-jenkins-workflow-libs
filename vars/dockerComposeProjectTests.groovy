import DockerCompose

def call(project, tag='latest')
{
    try {
        def String container = "${project}_ci_project_tests"
        sh "docker rm ${container} || true"

        sh DockerCompose
            .command('run')
            .withEnvironment('IMAGE_TAG', tag)
            .withOption('name', container)
            .withArgument('ci')
            .withArgument('./project_tests.sh')
            .toString()

        // TODO: extract in dockerComposeSmokeTests
        // docker-compose up
        // optionally docker wait
    } finally {
        // docker cp test artifacts
        // docker compose down
    }
}
