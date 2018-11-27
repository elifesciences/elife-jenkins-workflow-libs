import org.junit.Test
import static org.junit.Assert.*
import Stack

class TestStack {

    @Test
    void should_parse_project_and_environment() throws Exception {
        assertEquals('elife-xpub', Stack.fromName('elife-xpub--ci').project())
        assertEquals('ci', Stack.fromName('elife-xpub--ci').environment())
    }
}
