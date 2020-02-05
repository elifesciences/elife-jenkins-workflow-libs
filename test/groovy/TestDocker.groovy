import org.junit.Test
import static org.junit.Assert.*
import Docker

class TestDocker {

    @Test
    void should_implement_a_simple_build_command() throws Exception {
        assertEquals(
            'docker build .',
            Docker
                .command('build')
                .withArgument('.')
                .toString()
        );
    }

    @Test
    void should_allow_short_options() throws Exception {
        assertEquals(
            'docker build -f Dockerfile.ci .',
            Docker
                .command('build')
                .withOption('f', 'Dockerfile.ci')
                .withArgument('.')
                .toString()
        );
    }

    @Test
    void should_allow_long_boolean_options() throws Exception {
        assertEquals(
            'docker build --pull .',
            Docker
                .command('build')
                .withOption('pull')
                .withArgument('.')
                .toString()
        );
    }

    @Test
    void should_allow_a_map_as_an_option() throws Exception {
        assertEquals(
            'docker build --build-arg question=unknown --build-arg answer=42 .',
            Docker
                .command('build')
                .withOption('build-arg', ['question':'unknown', 'answer': 42])
                .withArgument('.')
                .toString()
        );
    }
}
