package examples;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import org.assertj.core.api.Assertions;
import org.devopsix.assertj.mail.MailAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

import static com.icegreen.greenmail.util.ServerSetupTest.SMTP;
import static java.time.OffsetDateTime.now;
import static java.time.OffsetDateTime.parse;
import static java.util.Collections.emptyMap;
import static jakarta.mail.Message.RecipientType.TO;
import static org.assertj.core.api.Assertions.as;

/**
 * This class demonstrates how the AssertJ Mail assertions can be used together
 * with GreenMail for verifying an email sent by an application.
 *
 * <p><a href="https://greenmail-mail-test.github.io/greenmail/">GreenMail</a> is an email server for testing purposes.</p>
 *
 * @author devopsix
 */
public class GreenMailExampleTest {

    // This extension will fire up an SMTP server listening at 127.0.0.1:3025
    // for every test.
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(SMTP);

    @BeforeEach
    public void sendMessage() throws Exception {
        // This is what your application code would do and what you usually
        // would invoke a method on the test subject for.
        // With GreenMail it does not matter whether your application uses JavaMail
        // or any other library. It would just have to send a message via SMTP.
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "127.0.0.1");
        properties.put("mail.smtp.port", "3025");
        Session session = Session.getInstance(properties);
        MimeMessage message = new MimeMessage(session);
        message.setFrom("bob@example.com");
        message.setRecipients(TO, "Anna <anna@example.com>");
        message.setSubject("Message from Bob");
        message.setHeader("X-Custom-Header", "Foo");
        message.addHeader("X-Custom-Multi-Header", "Foo");
        message.addHeader("X-Custom-Multi-Header", "Bar");
        message.addHeader("Resent-Date", "Wed, 1 Dec 82 14:49:44 +0100");
        message.addHeader("Resent-Date", "Sun, 1 Dec 2019 14:49:44 +0100");
        message.setText("Lorem ipsum");
        Transport.send(message);
    }

    @Test
    public void messageShouldHaveFromHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).from().singleElement(MailAssertions.INTERNET_ADDRESS)
            .address().isEqualTo("bob@example.com");
    }

    @Test
    public void messageShouldHaveDateHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).date().isBetween(now().minusMinutes(1L), now());
    }

    @Test
    public void messageShouldNotHaveSenderHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).sender().isNull();
    }

    @Test
    public void messageShouldNotHaveReplyToHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).replyTo().isNull();
    }

    @Test
    public void messageShouldHaveToHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).to().singleElement(as(MailAssertions.INTERNET_ADDRESS))
            .address().isEqualTo("anna@example.com");
        MailAssertions.assertThat(message).to().singleElement(as(MailAssertions.INTERNET_ADDRESS))
            .personal().isEqualTo("Anna");
    }

    @Test
    public void messageShouldNotHaveCcHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).cc().isNull();
    }

    @Test
    public void messageShouldNotHaveBccHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).bcc().isNull();
    }

    @Test
    public void messageShouldHaveReceivedHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).headerValues("Received").hasSize(1);
    }

    @Test
    public void messageShouldHaveSubjectHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).subject().isEqualTo("Message from Bob");
    }

    @Test
    public void messageShouldHaveCustomHeader() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).headerValue("X-Custom-Header").isEqualTo("Foo");
    }

    @Test
    public void messageShouldHaveMultipleCustomHeaders() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).headerValues("X-Custom-Multi-Header")
            .containsExactlyInAnyOrder("Foo", "Bar");
    }

    @Test
    public void messageShouldHaveMultipleResentDateHeaders() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).dateHeaderValues("Resent-Date")
            .containsExactlyInAnyOrder(
                parse("1982-12-01T14:49:44+01:00"),
                parse("2019-12-01T14:49:44+01:00")
            );
    }

    @Test
    public void messageShouldNotHaveDkimSignature() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).headerValue("DKIM-Signature").isNull();
        MailAssertions.assertThat(message).dkimSignature(emptyMap()).records().isNull();
    }

    @Test
    public void messageShouldHaveTextBody() {
        MimeMessage message = getReceivedMessage();
        MailAssertions.assertThat(message).textContent().contains("Lorem ipsum");
    }

    private MimeMessage getReceivedMessage() {
        MimeMessage[] messages = greenMail.getReceivedMessages();
        Assertions.assertThat(messages).hasSize(1);
        return messages[0];
    }
}
