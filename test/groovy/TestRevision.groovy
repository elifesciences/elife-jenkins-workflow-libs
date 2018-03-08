import org.junit.Test
import static org.junit.Assert.*
import Revision

class TestRevision {

    @Test
    void should_understand_if_it_is_a_branch() throws Exception {
        assertFalse((new Revision('1f14000695d4d2575cc0be4500c18a5af44d9da8')).isBranch())
        assertFalse((new Revision('ffffffffffffffffffffffffffffffffffffffff')).isBranch())
        assertFalse((new Revision('0000000000000000000000000000000000000000')).isBranch())
        assertTrue((new Revision('develop')).isBranch())
        assertTrue((new Revision('approved')).isBranch())
        assertTrue((new Revision('branch')).isBranch())
    }
}
