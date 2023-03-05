package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.devopsix.assertj.mail.MultipartCreator.newMultipart;
import static org.devopsix.assertj.mail.PartCreator.newPart;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import org.junit.jupiter.api.Test;

public class MultipartAssertPartsTest {

  @Test
  public void onePart() {
    BodyPart part = newPart().text("Lorem ipsum").create();
    Multipart multipart = newMultipart().subtype("mixed").bodyParts(part).create();
    Message message = newMessage().multipart(multipart).create();
    MailAssertions.assertThat(message).multipartContent().parts().hasSize(1);
  }

  @Test
  public void twoParts() {
    BodyPart textPart = newPart().text("Lorem ipsum").create();
    BodyPart htmlPart =
        newPart().header("Content-Type", "text/html").text("<html>Lorem ipsum</html>").create();
    Multipart multipart =
        newMultipart().subtype("alternative").bodyParts(textPart, htmlPart).create();
    Message message = newMessage().multipart(multipart).create();
    MailAssertions.assertThat(message).multipartContent().parts().hasSize(2);
  }

  @Test
  public void nestedParts() {
    BodyPart part = newPart().text("Lorem ipsum").create();
    Multipart multipartInner = newMultipart().subtype("mixed").bodyParts(part).create();
    Multipart multipartOuter = newMultipart().subtype("mixed")
        .bodyParts(newPart().multipart(multipartInner).create()).create();
    Message message = newMessage().multipart(multipartOuter).create();
    MailAssertions.assertThat(message).multipartContent().parts().hasSize(1);
    MailAssertions.assertThat(message).multipartContent().parts().singleElement(MailAssertions.PART)
        .multipartContent().parts().hasSize(1);
  }
}
