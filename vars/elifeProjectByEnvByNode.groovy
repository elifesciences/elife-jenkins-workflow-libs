def call(project, env_csv, closure) {
    for (env in env_csv.split(",")) {
        env = env.trim()
        def stackname = "${project}--${env}"
        def Integer node_count = builderRunTask("report.ec2_node_count", stackname) as Integer
        if (node_count > 0) {
            // "project--prod"
            lock(stackname) {
                for (int node = 1; node <= node_count; node++) {
                    // "project--prod--1"
                    def stage_label = "${stackname}--${node}"
                    if (node_count == 1) {
                        // "project--prod" (cuts down on some noise)
                        stage_label = stackname
                    }
                    stage(stage_label, {
                        builderRunTask("aws.ec2.start_node", stackname, node as String)
                        closure(project, env, node as String)
                    });
                };
            }
        }
    };
};
