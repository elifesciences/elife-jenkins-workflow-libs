def call(applicationId, allowedStatuses) {
    def currentStatus = elifeNewRelicStatus(applicationId)
    if (allowedStatuses.contains(currentStatus)) {
        echo "New Relic status for ${applicationId} was expected to be in ${allowedStatuses}, it is `${currentStatus}`"
    } else {
        throw new Exception("New Relic status for ${applicationId} was expected to be in ${allowedStatuses}, but it is `${currentStatus}`")
    }
}
