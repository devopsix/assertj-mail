package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.Message;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Test;

public class MessageAssertHeaderValuesTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("Custom-Header"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(message)
        .headerValues("Custom-Header").anySatisfy((f) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Message message = newMessage().create();
    MailAssertions.assertThat(message).headerValues("Custom-Header").isNull();
  }

  @Test
  public void headersArePresent() {
    Message message = newMessage()
        .header("Custom-Header", "Foo")
        .header("Custom-Header", "Bar").create();
    MailAssertions.assertThat(message).headerValues("Custom-Header")
        .hasSize(2)
        .contains("Foo", "Bar");
  }
}
