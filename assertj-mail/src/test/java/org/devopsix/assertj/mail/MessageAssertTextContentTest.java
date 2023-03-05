package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.Message;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Test;

public class MessageAssertTextContentTest {

  @Test
  public void contentCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getContent()).thenThrow(new MessagingException("error decoding header"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).textContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsMissing() throws Exception {
    Message message = mock(Message.class);
    when(message.getContent()).thenReturn(null);
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).textContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsPresent() {
    Message message = newMessage().text("Lorem ipsum").create();
    MailAssertions.assertThat(message).textContent().isEqualTo("Lorem ipsum");
  }
}
