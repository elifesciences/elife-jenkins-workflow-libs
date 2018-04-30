def call(stackname, command, folder, label) {
    echo "builderCmdForProjectTests: ${stackname}, ${command}, ${label}"
    builderCmd stackname, "cd ${folder}; ${command}"
}
