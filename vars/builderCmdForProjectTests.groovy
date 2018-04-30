def call(stackname, command, label) {
    echo "builderCmdForProjectTests: ${stackname}, ${command}, ${label}"
    builderCmd stackname, command
}
