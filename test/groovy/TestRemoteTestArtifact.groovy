import org.junit.Test
import static org.junit.Assert.*
import RemoteTestArtifact

class TestRemoteTestArtifact {

    @Test
    void calculates_local_paths_from_relative_path() throws Exception {
        def a = new RemoteTestArtifact('build/phpunit.xml');
        assertEquals('build/', a.localTestArtifactFolder())
        assertEquals('build/phpunit.xml', a.localTestArtifact())
    }

    @Test
    void calculates_local_paths_from_absolute_path() throws Exception {
        def a = new RemoteTestArtifact('/srv/journal/build/phpunit.xml');
        assertEquals('build/', a.localTestArtifactFolder())
        assertEquals('build/phpunit.xml', a.localTestArtifact())
    }
}

