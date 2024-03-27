This repository contains the eLife custom steps for Jenkins 2 pipelines (built with the Pipeline plugin, formerly known 
as Workflow).

## Rationale

Jenkins supports the definition of pipelines through code, in the form of Groovy scripts. 
Pipelines can check out a project, deploy it on different environments, build artifacts, run tests.

Jenkins pipelines can be specified either through the Jenkins user interface (which will store them in the giant pile 
of XML™) or inside projects in a top-level `Jenkinsfile`. In both cases they may become longer and longer and contain 
duplicated code from other projects.

Jenkins however provides the possibility to define custom steps shared by all pipelines, in the form of a Git 
repository (this one).

The `master` branch of this repository is checked out before any job is run and is available within `Jenkinsfile` files
'implicity' - without any need to explicitly include it.

This behaviour is configured in Jenkins under "Global Pipeline Libraries" here: https://alfred.elifesciences.org/manage/configure
