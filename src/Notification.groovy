public class Notification implements Serializable {
    public static final String SLACK = 'slack'
    public static final String EMAIL = 'email'
    private final String type
    private final String value

    public static fromMaintainersFileValue(value) {
        if (value.contains("#")) { 
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
