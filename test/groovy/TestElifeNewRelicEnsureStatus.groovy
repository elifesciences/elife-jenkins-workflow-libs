import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test

class TestExampleJob extends BasePipelineTest {

    @Override
    @Before
    void setUp() throws Exception {
        super.setUp()
    }

    @Test
    void should_execute_without_errors() throws Exception {
        def script = runScript("samples/docker.groovy")
        script.execute();
        printCallStack()
    }
}

