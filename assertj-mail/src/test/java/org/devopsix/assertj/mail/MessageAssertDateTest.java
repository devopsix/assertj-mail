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

public class MessageAssertDateTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("Date"))).thenThrow(new MessagingException("error decoding header"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).date().satisfies((d) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Message message = newMessage()
        .header("Date", now())
        .header("Date", now())
        .create();
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).date().satisfies((d) -> {
        }));
  }

  @Test
  public void headerValueIsMalformed() {
    Message message = newMessage()
        .header("Date", "foobar")
        .create();
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).date().satisfies((d) -> {
        }));
  }

  @Test
  public void headerIsMissing() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("Date"))).thenReturn(null);
    MailAssertions.assertThat(message).date().isNull();
  }

  @Test
  public void headerIsPresent() {
    OffsetDateTime date = now();
    Message message = newMessage().date(date).create();
    MailAssertions.assertThat(message).date().isEqualToIgnoringNanos(date);
  }
}
