package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.Message;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Test;

public class MessageAssertSubjectTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("Subject"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).subject().satisfies((s) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Message message = newMessage()
        .header("Subject", "Foo")
        .header("Subject", "Bar")
        .create();
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).subject().satisfies((s) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Message message = newMessage().subject(null).create();
    MailAssertions.assertThat(message).subject().isNull();
  }

  @Test
  public void headerIsPresent() {
    Message message = newMessage().subject("Foo").create();
    MailAssertions.assertThat(message).subject().isEqualTo("Foo");
  }
}
