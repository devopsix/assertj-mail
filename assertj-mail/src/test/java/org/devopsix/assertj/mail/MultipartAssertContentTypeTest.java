package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.devopsix.assertj.mail.MultipartCreator.newMultipart;
import static org.devopsix.assertj.mail.PartCreator.newPart;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import org.junit.jupiter.api.Test;

public class MultipartAssertContentTypeTest {

  @Test
  public void contentTypeMixed() {
    BodyPart part = newPart().text("Lorem ipsum").create();
    Multipart multipart = newMultipart().subtype("mixed").bodyParts(part).create();
    Message message = newMessage().multipart(multipart).create();
    MailAssertions.assertThat(message).multipartContent().contentType()
        .startsWith("multipart/mixed;");
  }

  @Test
  public void contentTypeAlternative() {
    BodyPart textPart = newPart().text("Lorem ipsum").create();
    BodyPart htmlPart =
        newPart().header("Content-Type", "text/html").text("<html>Lorem ipsum</html>").create();
    Multipart multipart =
        newMultipart().subtype("alternative").bodyParts(textPart, htmlPart).create();
    Message message = newMessage().multipart(multipart).create();
    MailAssertions.assertThat(message).multipartContent().contentType()
        .startsWith("multipart/alternative;");
  }
}
