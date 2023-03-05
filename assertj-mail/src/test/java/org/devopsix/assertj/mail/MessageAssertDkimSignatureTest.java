package org.devopsix.assertj.mail;

import static java.time.OffsetDateTime.now;
import static java.util.Collections.singletonMap;
import static javax.mail.Message.RecipientType.TO;
import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.devopsix.assertj.mail.MessageLoader.loadMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;

public class MessageAssertDkimSignatureTest {

  private static final KeyPair keyPair = generateKeyPair();

  @Test
  public void signatureIsValid() {
    MimeMessage message = createMessage();
    String publicKey = "k=rsa; p=" + base64(keyPair.getPublic());
    Map<String, String> publicKeys = singletonMap("foo._domainkey.example.com", publicKey);
    MailAssertions.assertThat(message).dkimSignature(publicKeys)
        .isValid()
        .records().isNotEmpty();
  }

  @Test
  public void signatureIsInvalidWithWrongKey() {
    MimeMessage message = createMessage();
    String wrongPublicKey = "k=rsa; p=" + base64(generateKeyPair().getPublic());
    Map<String, String> publicKeys = singletonMap("foo._domainkey.example.com", wrongPublicKey);
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).dkimSignature(publicKeys).isValid());
  }

  @Test
  public void signatureIsInvalidWithTamperedFromHeader() throws Exception {
    MimeMessage message = createMessage();
    String publicKey = "k=rsa; p=" + base64(keyPair.getPublic());
    Map<String, String> publicKeys = singletonMap("foo._domainkey.example.com", publicKey);
    message.setFrom("evil@example.com");
    final MimeMessage tamperedMessage = writeAndReread(message);
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(tamperedMessage).dkimSignature(publicKeys).isValid());
  }

  @Test
  public void signatureIsInvalidWithTamperedToHeader() throws Exception {
    MimeMessage message = createMessage();
    String publicKey = "k=rsa; p=" + base64(keyPair.getPublic());
    Map<String, String> publicKeys = singletonMap("foo._domainkey.example.com", publicKey);
    MailAssertions.assertThat(message).dkimSignature(publicKeys).isValid();
    message.setRecipient(TO, new InternetAddress("evil@example.com"));
    final MimeMessage tamperedMessage = writeAndReread(message);
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(tamperedMessage).dkimSignature(publicKeys).isValid());
  }

  @Test
  public void signatureIsInvalidWithTamperedSubjectHeader() throws Exception {
    MimeMessage message = createMessage();
    String publicKey = "k=rsa; p=" + base64(keyPair.getPublic());
    Map<String, String> publicKeys = singletonMap("foo._domainkey.example.com", publicKey);
    MailAssertions.assertThat(message).dkimSignature(publicKeys).isValid();
    message.setSubject("Evil");
    final MimeMessage tamperedMessage = writeAndReread(message);
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(tamperedMessage).dkimSignature(publicKeys).isValid());
  }

  @Test
  public void signatureIsValidGmailMessageSignature() {
    // This is the DKIM public key as published by Google for gmail.com at the time
    // the test message was recorded.
    final Map<String, String> gmailPublicKey = singletonMap("20161025._domainkey.gmail.com",
        "k=rsa; p=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAviPGBk4ZB64UfSqWyAicdR7lodhy"
            + "tae+EYRQVtKDhM+1mXjEqRtP/pDT3sBhazkmA48n2k5NJUyMEoO8nc2r6sUA+/Dom5jRBZp6qDKJOwj"
            + "J5R/OpHamlRG+YRJQqRtqEgSiJWG7h7efGYWmh4URhFM9k9+rmG/CwCgwx7Et+c8OMlngaLl04/bPmf"
            + "pjdEyLWyNimk761CX6KymzYiRDNz1MOJOJ7OzFaS4PFbVLn0m5mf0HVNtBpPwWuCNvaFVflUYxEyblb"
            +
            "B6h/oWOPGbzoSgtRA47SHV53SwZjIsVpbq4LxUW9IxAEwYzGcSgZ4n5Q8X8TndowsDUzoccPFGhdwIDAQAB");
    MimeMessage message = loadMessage("message-gmail.txt");
    MailAssertions.assertThat(message).dkimSignature(gmailPublicKey).isValid();
  }

  @Test
  public void signatureIsValidOutlookMessageSignature() {
    // This is the DKIM public key as published by Microsoft for outlook.com at the time
    // the test message was recorded.
    final Map<String, String> msPublicKey = singletonMap("selector1._domainkey.outlook.com",
        "v=DKIM1; k=rsa; p=MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvWyktrIL8DO/+UGvMbv"
            + "7cPd/Xogpbs7pgVw8y9ldO6AAMmg8+ijENl/c7Fb1MfKM7uG3LMwAr0dVVKyM+mbkoX2k5L7lsROQr0"
            + "Z9gGSpu7xrnZOa58+/pIhd2Xk/DFPpa5+TKbWodbsSZPRN8z0RY5x59jdzSclXlEyN9mEZdmOiKTsOP"
            + "6A7vQxfSya9jg5N81dfNNvP7HnWejMMsKyIMrXptxOhIBuEYH67JDe98QgX14oHvGM2Uz53if/SW8MF"
            + "09rYh9sp4ZsaWLIg6T343JzlbtrsGRGCDJ9JPpxRWZimtz+Up/BlKzT6sCCrBihb/Bi3pZiEBB4Ui/v"
            + "ruL5RCQIDAQAB; n=2048,1452627113,1468351913");
    MimeMessage message = loadMessage("message-outlook.txt");
    MailAssertions.assertThat(message).dkimSignature(msPublicKey).isValid();
  }

  private MimeMessage createMessage() {
    return newMessage()
        .date(now())
        .from("joe.average@example.com")
        .to("joe.average@example.com")
        .subject("Message from Joe")
        .text("Lorem ipsum")
        .dkimSignature(keyPair, "example.com", "foo",
            List.of("mime-version", "from", "date", "message-id", "subject", "to"))
        .create();
  }

  private MimeMessage writeAndReread(Message message) {
    try {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      message.writeTo(buffer);
      Session session = Session.getDefaultInstance(new Properties());
      return new MimeMessage(session, new ByteArrayInputStream(buffer.toByteArray()));
    } catch (IOException | MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  private String base64(PublicKey key) {
    return Base64.getEncoder().encodeToString(key.getEncoded());
  }

  private static KeyPair generateKeyPair() {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      keyGen.initialize(2048);
      return keyGen.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
