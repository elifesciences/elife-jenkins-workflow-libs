import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Before
import org.junit.Test
import java.util.Map
import java.util.LinkedHashMap
import org.codehaus.groovy.runtime.GStringImpl

import static com.lesfurets.jenkins.unit.global.lib.LibraryConfiguration.library
import static com.lesfurets.jenkins.unit.global.lib.ProjectSource.projectSource
import static com.lesfurets.jenkins.unit.MethodSignature.method
import java.lang.reflect.*;

class TestDockerIntegration extends BasePipelineTest {

    String sharedLibs = '/home/giorgio/code/elife-jenkins-workflow-libs';

    @Override
    @Before
    void setUp() throws Exception {
        this.scriptRoots += 'vars/'
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
        def library = library().name('elife-jenkins-workflow-libs')
                        .defaultVersion('master')
                        .allowOverride(true)
                        .implicit(true)
                        .targetPath('<notNeeded>')
                        .retriever(projectSource(sharedLibs))
                        .build()
        helper.registerSharedLibrary(library)
		//helper.registerAllowedMethod('echo', [Object.class], {})
        //binding.setVariable('env', [:])

        //def myscript = loadScript('sayHello.groovy')
        //myscript.call("aaa")
        //def myscript = runScript('sayHello.groovy')
        //myscript.call("aaa")

        def sayHelloUpper = runScript('sayHelloUpper.groovy')
        sayHelloUpper.call('bbb')
		printCallStack()

		//def intercepted = helper.getAllowedMethodEntry('echo', [GStringImpl.class])
		//println(intercepted)
		//println(intercepted.value)
		//def script = runScript("samples/project_tests.groovy")
		//script.execute()
		//printCallStack()

    }
}

