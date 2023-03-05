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

public class MessageAssertIsMultipartTest {

  @Test
  public void contentCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getContent()).thenThrow(new MessagingException("error decoding content"));
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(message).isMultipart());
  }

  @Test
  public void contentIsMissing() {
    Message message = newMessage().create();
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(message).isMultipart());
  }

  @Test
  public void contentIsPresent() {
    BodyPart part = newPart().text("Lorem ipsum").create();
    Multipart multipart = newMultipart().bodyParts(part).create();
    Message message = newMessage().multipart(multipart).create();
    MailAssertions.assertThat(message).isMultipart();
  }

  @Test
  public void contentIsNotMultipart() {
    Message message = newMessage().text("Lorem ipsum").create();
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(message).isMultipart());
  }
}
