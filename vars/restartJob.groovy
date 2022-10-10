elifePipeline({
    def String project = params.project; // "lax", "journal-cms", ...
    def String env_csv = params.envlist; // "ci,continuumtest,end2end"
    println("got project:${project}")
    println("got envs:${env_csv}")
    for (env in env_csv.split(",")) {
        env = env.trim()
        def stackname = "${project}--${env}";
        println("got stackname:${stackname}")
        
        def Integer node_count = builderRunTask("report.ec2_node_count", stackname) as Integer;
        println("got nodecount:${node_count}")
        if (node_count > 0) {
            lock(stackname) {
                for (Integer node in 1 .. node_count) {
                    println("got node:${node}")
                    // "prod node 1"
                    stage("${stackname} node ${node}", {
                            builderRunTask("aws.ec2.start", stackname, node)
                            println("doing a thing")
                            result = builderCmdNode(stackname, node, "cat /etc/salt/minion_id", null, captureOutput=true)
                            println("result:${result}")
                            println("done a thing")
                            
                            //builderCmdNode(stackname, node, "sudo /usr/local/bin/daily-security-update");
                            //builderRunTask("aws.ec2.restart", stackname, node)
                            //builderCmdNode(stackname, node, "sudo /usr/local/bin/daily-system-update");
                            
                    });
                };
            }
        }
    };
});
