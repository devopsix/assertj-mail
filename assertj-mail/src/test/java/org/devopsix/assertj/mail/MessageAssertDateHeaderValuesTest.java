package org.devopsix.assertj.mail;

import static java.time.OffsetDateTime.now;
import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.Message;
import javax.mail.MessagingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageAssertDateHeaderValuesTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getHeader(eq("Resent-Date"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(message)
        .dateHeaderValues("Resent-Date").anySatisfy((f) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Message message = newMessage().create();
    MailAssertions.assertThat(message).dateHeaderValues("Resent-Date").isNull();
  }

  @Test
  public void headersArePresent() {
    Message message = newMessage()
        .header("Resent-Date", now())
        .header("Resent-Date", now()).create();
    MailAssertions.assertThat(message).dateHeaderValues("Resent-Date")
        .hasSize(2)
        .allSatisfy(
            date -> Assertions.assertThat(date).isStrictlyBetween(now().minusMinutes(1), now()));
  }
}
