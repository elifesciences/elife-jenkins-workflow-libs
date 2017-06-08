import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test

class TestExampleJob extends BasePipelineTest {

    @Override
    @Before
    void setUp() throws Exception {
        super.setUp()
        // Assigns false to a job parameter ENABLE_TEST_STAGE 
        // binding.setVariable('ENABLE_TEST_STAGE', 'false')
        // Defines the previous execution status
        // binding.getVariable('currentBuild').previousBuild = [result: 'UNSTABLE']
    }

    @Test
    void should_execute_without_errors() throws Exception {
        def script = loadScript("vars/elifeNewRelicEnsureStatus.groovy")
        System.out.println(script);
        printCallStack()
    }
}

