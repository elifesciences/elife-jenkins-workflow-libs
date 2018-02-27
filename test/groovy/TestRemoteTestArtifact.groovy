import org.junit.Test
import static org.junit.Assert.*
import RemoteTestArtifact

class TestRemoteTestArtifact {

    @Test
    void something() throws Exception {
        def a = new RemoteTestArtifact('build/phpunit.xml');
        assertEquals('build/', a.localTestArtifactFolder())
    }
}

