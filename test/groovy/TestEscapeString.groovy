import org.junit.Test
import static org.junit.Assert.*
import EscapeString

class TestEscapeString {

    @Test
    void for_bash_single_quotes() throws Exception {
        assertEquals(
            "it'\\''s a wonderful pipeline",
            EscapeString.forBashSingleQuotes("it's a wonderful pipeline")
        )
    }
}

