def call(String task, String ... args) {

    // we want to fail if the command fails, return the stdout if it succeeds.
    // we can't have both unfortunately:
    // - https://issues.jenkins.io/browse/JENKINS-44930
    // one strategy is to send stdout to a file and then check the return code:
    // def output_file = ".stdout.txt" // but this is no good.
    // multiple sequential calls will overwrite this file, and multiple calls in 
    // parallel and it becomes non-deterministic or worse, corrupt.
    // so we'll generate a random string to create a unique file:
    // - https://unix.stackexchange.com/questions/230673/how-to-generate-a-random-string#answer-230676
    def random_string = sh(script:"tr -dc A-Za-z0-9 </dev/urandom | head -c 13", returnStdout:true)
    def output_file = ".stdout.${random_string}.txt"

    // "/path/to/bldr sometask"
    def shell_cmd = "${env.BUILDER_PATH}bldr ${task}"
    if (args) {
        // "/path/to/bldr sometask:somearg1,somearg2"
        shell_cmd += ":" + _escapeCmd(args.join(","))
    }
    // print stdout as it appears but also capture to file
    // "/path/to/bldr sometask:somearg1 | tee .stdout.dwG76zKTGZeOI.txt"
    shell_cmd += " | tee " + output_file

    // disable input. this should be set by the shell, but just in case
    non_interactive = "BUILDER_NON_INTERACTIVE=1 "
    shell_cmd = non_interactive + shell_cmd

    rc = sh(script:shell_cmd, returnStatus:true) as Integer
    if (rc == 0) {
        return readFile(output_file).trim()
    }

    def error_msg = "builder task '${task}' failed: ${rc}"
    if (args) {
        error_msg = "builder task '${task}' with args ('${args}') failed: ${rc}"
    }
    error(error_msg)
}
