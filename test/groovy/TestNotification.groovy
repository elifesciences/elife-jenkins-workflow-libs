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
    void should_recognize_slack_channels_using_an_host_and_an_hash() throws Exception {
        def notification = Notification.fromMaintainersFileValue("elifesciences.slack.com#tech-team")
        assertEquals Notification.SLACK, notification.type()
        assertEquals "#tech-team", notification.value()
    }

    @Test
    void should_recognize_slack_channels_using_a_hash() throws Exception {
        def notification = Notification.fromMaintainersFileValue("#tech-team")
        assertEquals Notification.SLACK, notification.type()
        assertEquals "#tech-team", notification.value()
    }

    @Test
    void should_skip_unsupported_values() throws Exception {
        def notification = Notification.fromMaintainersFileValue("gibberish")
        assertEquals Notification.NONE, notification.type()
        assertEquals "gibberish", notification.value()
    }
}

