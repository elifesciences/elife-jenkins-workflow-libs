import org.junit.Test
import static org.junit.Assert.*
import Notification

class TestNotification {
    @Test
    void should_recognize_email_addresses() throws Exception {
        def notification = Notification.fromMaintainersFileValue("foo@example.com")
        assertEquals Notification.EMAIL, notification.type()
        assertEquals "foo@example.com", notification.value()
    }

    @Test
    void should_recognize_slack_channels() throws Exception {
        def notification = Notification.fromMaintainersFileValue("#xpub-tech")
        assertEquals Notification.SLACK, notification.type()
        assertEquals "#xpub-tech", notification.value()
    }
}

