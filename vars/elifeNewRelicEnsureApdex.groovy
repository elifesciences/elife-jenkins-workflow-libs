def call(applicationId, minimum=0.85) {
    def currentApdex = elifeNewRelicApdex(applicationId)
    if (currentApdex < minimum) {
        throw new Exception("New Relic APM apdex for ${applicationId} was required to be > ${minimum}, but it is ${currentApdex}")
    } else {
        echo "New Relic APM apdex for ${applicationId} was required to be > ${minimum}, it is ${currentApdex}"
    }
}
