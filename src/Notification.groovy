public class Notification implements Serializable {
    public static final String SLACK = 'slack'
    public static final String EMAIL = 'email'
    private static final String SLACK_HOST_FOR_REGEX = 'elifesciences\\.slack\\.com'
    private final String type
    private final String value

    public static fromMaintainersFileValue(value) {
        def slackChannelMatcher = value =~ /${SLACK_HOST_FOR_REGEX}(#.+)/
        if (slackChannelMatcher) {
            return new Notification(SLACK, slackChannelMatcher[0][1])
        } else if (value.contains("#")) { 
            return new Notification(SLACK, value)
        } else {
            return new Notification(EMAIL, value)
        }
    }

    public Notification(type, value) {
        this.type = type
        this.value = value
    }

    public String type()
    {
        return this.type
    }

    public String value()
    {
        return this.value
    }
}
