package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.devopsix.assertj.mail.MultipartCreator.newMultipart;
import static org.devopsix.assertj.mail.PartCreator.newPart;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import org.junit.jupiter.api.Test;

public class MessageAssertMultipartContentsTest {

  @Test
  public void contentCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getContent()).thenThrow(new MessagingException("error decoding content"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).multipartContents().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsPresent() {
    BodyPart part = newPart().text("Lorem ipsum").create();
    Multipart multipartInner = newMultipart().subtype("mixed").bodyParts(part).create();
    Multipart multipartOuter = newMultipart().subtype("mixed")
        .bodyParts(newPart().multipart(multipartInner).create()).create();
    Message message = newMessage().multipart(multipartOuter).create();
    MailAssertions.assertThat(message).multipartContents().hasSize(2);
  }
}
