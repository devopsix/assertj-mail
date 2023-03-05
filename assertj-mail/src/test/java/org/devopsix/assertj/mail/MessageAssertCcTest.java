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

public class MessageAssertCcTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("Cc"))).thenThrow(new MessagingException("error decoding header"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).cc().anySatisfy((c) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Message message = newMessage()
        .header("Cc", "anna@example.com")
        .header("Cc", "bob@example.com")
        .create();
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).cc().anySatisfy((c) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Message message = newMessage().cc((Collection<String>) null).create();
    MailAssertions.assertThat(message).cc().isNull();
  }

  @Test
  public void headerIsPresent() {
    Message message = newMessage().cc("anna@example.com").create();
    MailAssertions.assertThat(message).cc()
        .singleElement(Assertions.as(MailAssertions.INTERNET_ADDRESS))
        .address().isEqualTo("anna@example.com");
  }

  @Test
  public void multiValueHeaderIsPresent() {
    Message message =
        newMessage().cc(List.of("Anna <anna@example.com>", "Bob <bob@example.com>")).create();
    MailAssertions.assertThat(message).cc()
        .hasSize(2)
        .extracting("address", "personal")
        .contains(tuple("anna@example.com", "Anna"), tuple("bob@example.com", "Bob"));
  }
}
