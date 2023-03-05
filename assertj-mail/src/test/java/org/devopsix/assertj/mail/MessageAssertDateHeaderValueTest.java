package org.devopsix.assertj.mail;

import static java.time.OffsetDateTime.now;
import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Test;

public class MessageAssertDateHeaderValueTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("Resent-Date"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(message)
        .dateHeaderValue("Resent-Date").satisfies((f) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Message message = newMessage()
        .header("Resent-Date", now())
        .header("Resent-Date", now())
        .create();
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(message)
        .dateHeaderValue("Resent-Date").satisfies((f) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Message message = newMessage().create();
    MailAssertions.assertThat(message).dateHeaderValue("Resent-Date").isNull();
  }

  @Test
  public void headerIsPresent() {
    OffsetDateTime date = now();
    Message message = newMessage().header("Resent-Date", date).create();
    MailAssertions.assertThat(message).dateHeaderValue("Resent-Date").isEqualToIgnoringNanos(date);
  }
}
