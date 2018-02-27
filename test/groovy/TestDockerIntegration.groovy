import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test
import java.util.Map
import java.util.LinkedHashMap
import org.codehaus.groovy.runtime.GStringImpl

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.ProjectSource.projectSource
import static com.lesfurets.jenkins.unit.MethodSignature.method

class TestDockerIntegration extends BasePipelineTest {

    String sharedLibs = '.';

    @Override
    @Before
    void setUp() throws Exception {
        super.setUp()
    }

    //@Test
    //void should_execute_without_errors() throws Exception {
    //    def script = runScript("samples/docker.groovy")
    //    script.execute()
    //    printCallStack()
    //}

    @Test
    void something() throws Exception {
		helper.registerAllowedMethod('echo', [Object.class], {})
        def library = library().name('commons')
                        .defaultVersion('<notNeeded>')
                        .allowOverride(true)
                        .implicit(true)
                        .targetPath('<notNeeded>')
                        .retriever(projectSource(sharedLibs))
                        .build()
        helper.registerSharedLibrary(library)
        binding.setVariable('env', [:])

		def intercepted = helper.getAllowedMethodEntry('echo', [GStringImpl.class])
		println(intercepted)
		println(intercepted.value)
		def script = runScript("samples/project_tests.groovy")
		script.execute()
		//printCallStack()
    }
}

