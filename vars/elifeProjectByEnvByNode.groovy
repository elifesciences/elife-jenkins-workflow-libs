def call(String project, String env_csv, Closure closure) {
    for (env in env_csv.split(",")) {
        env = env.trim()
        def stackname = "${project}--${env}"
        def Integer node_count = builderRunTask("report.ec2_node_count", stackname) as Integer
        if (node_count > 0) {
            // "project--prod"
            lock(stackname) {
                builderStart(stackname)
                for (int node = 1; node <= node_count; node++) {
                    // "project--prod--1"
                    def stage_label = "${stackname}--${node}"
                    if (node_count == 1) {
                        // "project--prod" (cuts down on some noise)
                        stage_label = stackname
                    }
                    stage(stage_label, {
                        closure(project, env, node)
                    });
                };
            }
        }
    };
};
