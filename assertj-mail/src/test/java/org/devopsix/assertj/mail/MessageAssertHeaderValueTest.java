package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.Message;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Test;

public class MessageAssertHeaderValueTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("X-Header"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(message)
        .headerValue("X-Header").satisfies((f) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Message message = newMessage()
        .header("X-Header", "Foo")
        .header("X-Header", "Bar")
        .create();
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(message)
        .headerValue("X-Header").satisfies((f) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Message message = newMessage().create();
    MailAssertions.assertThat(message).headerValue("X-Header").isNull();
  }

  @Test
  public void headerIsPresent() {
    Message message = newMessage().header("X-Header", "Foo").create();
    MailAssertions.assertThat(message).headerValue("X-Header").isEqualTo("Foo");
  }
}
