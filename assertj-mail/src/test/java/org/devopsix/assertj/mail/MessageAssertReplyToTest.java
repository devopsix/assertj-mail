package org.devopsix.assertj.mail;

import static org.assertj.core.api.Assertions.tuple;
import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageAssertReplyToTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("Reply-To"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).replyTo().anySatisfy((r) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Message message = newMessage()
        .header("Reply-To", "anna@example.com")
        .header("Reply-To", "bob@example.com")
        .create();
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).replyTo().anySatisfy((r) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Message message = newMessage().replyTo((Collection<String>) null).create();
    MailAssertions.assertThat(message).replyTo().isNull();
  }

  @Test
  public void headerIsPresent() {
    Message message = newMessage().replyTo("anna@example.com").create();
    MailAssertions.assertThat(message).replyTo()
        .singleElement(Assertions.as(MailAssertions.INTERNET_ADDRESS))
        .address().isEqualTo("anna@example.com");
  }

  @Test
  public void multiValueHeaderIsPresent() {
    Message message =
        newMessage().replyTo(List.of("Anna <anna@example.com>", "Bob <bob@example.com>")).create();
    MailAssertions.assertThat(message).replyTo()
        .hasSize(2)
        .extracting("address", "personal")
        .contains(tuple("anna@example.com", "Anna"), tuple("bob@example.com", "Bob"));
  }
}
