This repository contains the eLife custom steps for Jenkins 2 pipelines (built with the Pipeline plugin, formerly known as Workflow).

## Rationale

Jenkins supports the definition of pipelines through code, in the form of Groovy scripts. Pipelines can check out a project, deploy it on different environments, build artifacts, run tests.

Jenkins pipelines can be specified either through the Jenkins user interface (which will store them in the giant pile of XML™) or inside projects in a top-level `Jenkinsfile`. In both cases they may become longer and longer and contain duplicated code from other projects.

Jenkins however provides the possibility to define custom steps shared by all pipelines, in the form of a Git repository. Jenkins implements [a ssh server](https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+SSH) that instead of giving you a shell access to its machine lets you pull and push from its own few repositories.

## Usage

1. Configure your Jenkins SSH access choosing a port at `http://$JENKINS/configure`.
2. Provide a public key at `http://$JENKINS/me/configure`.
3. Git push this repository (or your fork of it) at the remote `ssh://$USERNAME@$JENKINS:$PORT/workflowLibs.git`.

`$PORT` should be the port you have chosen (e.g. 16022) and `$USERNAME` should be your Jenkins username.


