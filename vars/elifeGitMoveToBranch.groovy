def call(commit, branch) {
    sh "git checkout ${branch}"
    sh "git reset --hard ${commit}"
    sh "git push origin ${branch}"
}
