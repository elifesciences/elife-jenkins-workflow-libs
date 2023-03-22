def call(String project_name, String envlist) {
    elifeProjectByEnvByNode(project_name, envlist, { project, env, node ->
        def stackname = "${project}--${env}"
        try {
            builderCmdNode(stackname, node, "sudo /usr/local/bin/daily-security-update");
        } catch (err) {
            println("call to 'daily-security-update' script failed: " + err.toString())
            println("ignoring and restarting instance.")
        }
        builderRunTask("aws.ec2.stop_node", stackname, node as String)
        builderStart(stackname)
        builderCmdNode(stackname, node, "sudo /usr/local/bin/daily-system-update");
    })
};
