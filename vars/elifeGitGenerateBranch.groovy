def call(prefix='generated_branch_') {
    def branch = "${prefix}${env.BUILD_NUMBER}"
    sh "git checkout -b ${branch}"
    return branch
}
