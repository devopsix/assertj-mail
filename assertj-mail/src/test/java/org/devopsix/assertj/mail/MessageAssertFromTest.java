package org.devopsix.assertj.mail;

import static org.assertj.core.api.Assertions.as;
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
import org.junit.jupiter.api.Test;

public class MessageAssertFromTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("From"))).thenThrow(new MessagingException("error decoding header"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).from().anySatisfy((f) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Message message = newMessage()
        .header("From", "anna@example.com")
        .header("From", "bob@example.com")
        .create();
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).from().anySatisfy((f) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Message message = newMessage().from((Collection<String>) null).create();
    MailAssertions.assertThat(message).from().isNull();
  }

  @Test
  public void headerIsPresent() {
    Message message = newMessage().from("anna@example.com").create();
    MailAssertions.assertThat(message).from()
        .singleElement(as(MailAssertions.INTERNET_ADDRESS))
        .address().isEqualTo("anna@example.com");
  }

  @Test
  public void multiValueHeaderIsPresent() {
    Message message =
        newMessage().from(List.of("Anna <anna@example.com>", "Bob <bob@example.com>")).create();
    MailAssertions.assertThat(message).from()
        .hasSize(2)
        .extracting("address", "personal")
        .contains(tuple("anna@example.com", "Anna"), tuple("bob@example.com", "Bob"));
  }
}
