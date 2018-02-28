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
}

