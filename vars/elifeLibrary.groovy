def call(Closure body, nodeName='elife-libraries--ci', timeoutInMinutes=120) {
    elifePipeline({
        elifeOnNode(body, nodeName)
    }, timeoutInMinutes)
}
