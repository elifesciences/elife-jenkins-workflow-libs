def call(hostname, folder) {
    sh "rsync -avz --exclude=/.git -e 'ssh -o StrictHostKeyChecking=no' elife@${hostname}:${folder} ."
}
