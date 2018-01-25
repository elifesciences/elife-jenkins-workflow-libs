import org.junit.Test
import DockerImage

class TestDockerImage {

    @Test
    void should_use_a_class() throws Exception {
        new DockerImage('something', 'elifesciences/php_cli', 'latest')
        System.out.println("class");
    }
}

