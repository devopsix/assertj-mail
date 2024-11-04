package examples;

import org.assertj.core.api.Assertions;
import org.devopsix.assertj.mail.MailAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Map;
import java.util.Properties;

import static java.util.Collections.singletonMap;

/**
 * This class demonstrates how the AssertJ Mail assertions can be used for
 * verifying an email loaded from a file.
 *
 * @author devopsix
 */
public class MessageFileExampleTest {

    private Message message;

    @BeforeEach
    public void loadMessage() throws IOException, MessagingException {
        try (InputStream messageStream = getClass().getClassLoader().getResourceAsStream("message.txt")) {
            Session session = Session.getDefaultInstance(new Properties());
            message = new MimeMessage(session, messageStream);
        }
    }

    @Test
    public void messageShouldHaveFromHeader() {
        MailAssertions.assertThat(message).from().singleElement(Assertions.as(MailAssertions.INTERNET_ADDRESS))
            .address().isEqualTo("devopsix@gmail.com");
        MailAssertions.assertThat(message).from().singleElement(Assertions.as(MailAssertions.INTERNET_ADDRESS))
            .personal().isEqualTo("devopsix");
    }

    @Test
    public void messageShouldHaveDateHeader() {
        MailAssertions.assertThat(message).date()
            .isEqualToIgnoringHours(LocalDate.of(2019, 12, 8).atTime(OffsetTime.MIN));
    }

    @Test
    public void messageShouldHaveMessageIdHeader() {
        MailAssertions.assertThat(message).headerValue("Message-Id").endsWith("@mail.gmail.com>");
    }

    @Test
    public void messageShouldHaveSubjectHeader() {
        MailAssertions.assertThat(message).subject().isEqualTo("Lörem Ipsüm");
    }

    @Test
    public void messageShouldHaveToHeader() {
        MailAssertions.assertThat(message).to().singleElement(Assertions.as(MailAssertions.INTERNET_ADDRESS))
            .address().isEqualTo("test-fbll2@mail-tester.com");
    }

    @Test
    public void messageShouldHaveReceivedHeaders() {
        MailAssertions.assertThat(message).headerValues("Received")
            .hasSize(3)
            .allMatch(r -> r.startsWith("from ") || r.startsWith("by "))
            .allMatch(r -> r.contains("mail-tester.com") || r.contains("google.com"));
    }

    @Test
    public void messageShouldHaveValidDkimSignature() {
        MailAssertions.assertThat(message).headerValue("DKIM-Signature").isNotEmpty();
        // This is the DKIM public key as published by Google for gmail.com at the time
        // the test message was recorded.
        // In your tests, replace with the key that that was actually used for signing your message.
        final Map<String, String> gmailPublicKey = singletonMap("20161025._domainkey.gmail.com",
            "k=rsa; p=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAviPGBk4ZB64UfSqWyAicdR7lodhy"
            + "tae+EYRQVtKDhM+1mXjEqRtP/pDT3sBhazkmA48n2k5NJUyMEoO8nc2r6sUA+/Dom5jRBZp6qDKJOwj"
            + "J5R/OpHamlRG+YRJQqRtqEgSiJWG7h7efGYWmh4URhFM9k9+rmG/CwCgwx7Et+c8OMlngaLl04/bPmf"
            + "pjdEyLWyNimk761CX6KymzYiRDNz1MOJOJ7OzFaS4PFbVLn0m5mf0HVNtBpPwWuCNvaFVflUYxEyblb"
            + "B6h/oWOPGbzoSgtRA47SHV53SwZjIsVpbq4LxUW9IxAEwYzGcSgZ4n5Q8X8TndowsDUzoccPFGhdwIDAQAB");
        MailAssertions.assertThat(message).dkimSignature(gmailPublicKey).isValid();
    }

    @Test
    public void messageShouldHaveMultipartContent() {
        MailAssertions.assertThat(message).isMultipart();
        MailAssertions.assertThat(message).multipartContent().contentType().startsWith("multipart/mixed;");
        MailAssertions.assertThat(message).multipartContent().parts().hasSize(2);
    }

    @Test
    public void messageShouldHaveAlternativeTextAndHtmlContent() {
        // The message has multipart/mixed content which contains another
        // multipart/alternative part which has a plain text and an HTML part.
        MailAssertions.assertThat(message).multipartContent().parts()
            .anySatisfy(part -> {
                MailAssertions.assertThat(part).multipartContent().contentType().startsWith("multipart/alternative;");
                MailAssertions.assertThat(part).multipartContent().parts()
                    .satisfiesExactly(
                        innerPart -> {
                            MailAssertions.assertThat(innerPart).headerValue("Content-Type").startsWith("text/plain;");
                            MailAssertions.assertThat(innerPart).textContent().contains("Lorem ipsum");
                        },
                        innerPart -> {
                            MailAssertions.assertThat(innerPart).headerValue("Content-Type").startsWith("text/html;");
                            MailAssertions.assertThat(innerPart).textContent().contains("Lorem ipsum");
                        }
                    );
            });
        // Match presence of multipart/alternative part independent of actual
        // message structure.
        MailAssertions.assertThat(message).multipartContents().anySatisfy(
            multipart -> {
                MailAssertions.assertThat(multipart).contentType().startsWith("multipart/alternative;");
                MailAssertions.assertThat(multipart).parts()
                    .satisfiesExactly(
                        part -> {
                            MailAssertions.assertThat(part).headerValue("Content-Type").startsWith("text/plain;");
                            MailAssertions.assertThat(part).textContent().contains("Lorem ipsum");
                        },
                        part -> {
                            MailAssertions.assertThat(part).headerValue("Content-Type").startsWith("text/html;");
                            MailAssertions.assertThat(part).textContent().contains("Lorem ipsum");
                        }
                    );

            }
        );
    }

    @Test
    public void messageShouldHaveImageAttachment() {
        // The message has multipart/mixed content which contains
        // the image attachment.
        MailAssertions.assertThat(message).multipartContent().parts()
            .anySatisfy(part -> {
                MailAssertions.assertThat(part).headerValue("Content-Type").startsWith("image/jpeg;");
                MailAssertions.assertThat(part).headerValue("Content-Disposition").contains("lena.jpg");
                MailAssertions.assertThat(part).binaryContent().hasSize(67683);
            });
    }
}
