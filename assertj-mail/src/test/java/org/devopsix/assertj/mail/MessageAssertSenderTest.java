package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.Message;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Test;

public class MessageAssertSenderTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("Sender"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).sender().satisfies((s) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Message message = newMessage()
        .header("Sender", "anna@example.com")
        .header("Sender", "bob@example.com")
        .create();
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).sender().satisfies((s) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Message message = newMessage().sender(null).create();
    MailAssertions.assertThat(message).sender().isNull();
    MailAssertions.assertThat(message).sender().address().isNull();
    MailAssertions.assertThat(message).sender().personal().isNull();
  }

  @Test
  public void addressOnlyHeaderIsPresent() {
    Message message = newMessage().sender("anna@example.com").create();
    MailAssertions.assertThat(message).sender().address().isEqualTo("anna@example.com");
  }

  @Test
  public void addressAndPersonalHeaderIsPresent() {
    Message message = newMessage().sender("Anna <anna@example.com>").create();
    MailAssertions.assertThat(message).sender().address().isEqualTo("anna@example.com");
    MailAssertions.assertThat(message).sender().personal().isEqualTo("Anna");
  }
}
