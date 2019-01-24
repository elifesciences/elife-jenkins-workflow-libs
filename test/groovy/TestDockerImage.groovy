import org.junit.Test
import static org.junit.Assert.*
import DockerImage

class TestDockerImage {

    @Test
    void should_expose_metadata() throws Exception {
        def i = new DockerImage('something', 'elifesciences/php_cli', 'latest')
        assertEquals('elifesciences/php_cli', i.repository())
        assertEquals('latest', i.tag())
        assertEquals('elifesciences/php_cli:latest', i.toString())
    }

}

