import org.junit.Test
import static org.junit.Assert.*
import DockerCompose

class TestDockerCompose {

    @Test
    void should_implement_a_build_command() throws Exception {
        assertEquals(
            'IMAGE_TAG=123456 docker-compose -f docker-compose.ci.yml build',
            DockerCompose
                .command('build')
                .withEnvironment('IMAGE_TAG', '123456')
                .toString()
        );
    }

    @Test
    void should_implement_a_run_command() throws Exception {
        assertEquals(
            'IMAGE_TAG=123456 docker-compose -f docker-compose.ci.yml run --name profiles_ci_project_tests ci ./project_tests.sh',
            DockerCompose
                .command('run')
                .withEnvironment('IMAGE_TAG', '123456')
                .withOption('name', 'profiles_ci_project_tests')
                .withArgument('ci')
                .withArgument('./project_tests.sh')
                .toString()
        );
    }
}

