def call(applicationId, allowedStatuses) {
    def currentStatus = elifeNewRelicStatus(applicationId)
    if (!allowedStatuses.contains(currentStatus)) {
        throw new Exception("New Relic status for ${applicationId} was expected to be in ${allowedStatuses}, but it is `${currentStatus}`")
    }
}
