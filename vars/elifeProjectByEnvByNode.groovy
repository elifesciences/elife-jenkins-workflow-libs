def call(project, env_csv, closure) {
    for (env in env_csv.split(",")) {
        env = env.trim()
        def stackname = "${project}--${env}";
        def Integer node_count = builderRunTask("report.ec2_node_count", stackname) as Integer;
        if (node_count > 0) {
            // "project--prod"
            lock(stackname) {
                for (Integer node in 1 .. node_count) {
                    // "project--prod--1"
                    stage("${stackname}--${node}", {
                        builderRunTask("aws.ec2.start", stackname, node)
                        closure(project, env, node)
                    });
                };
            }
        }
    };
};
