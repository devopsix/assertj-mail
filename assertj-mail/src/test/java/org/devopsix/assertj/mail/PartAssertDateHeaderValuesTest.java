package org.devopsix.assertj.mail;

import static java.time.OffsetDateTime.now;
import static org.devopsix.assertj.mail.PartCreator.newPart;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.MessagingException;
import javax.mail.Part;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class PartAssertDateHeaderValuesTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Part part = mock(Part.class);
    when(part.getHeader(eq("Resent-Date"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(part)
        .dateHeaderValues("Resent-Date").anySatisfy((f) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Part part = newPart().create();
    MailAssertions.assertThat(part).dateHeaderValues("Resent-Date").isNull();
  }

  @Test
  public void headersArePresent() {
    Part part = newPart()
        .header("Resent-Date", now())
        .header("Resent-Date", now()).create();
    MailAssertions.assertThat(part).dateHeaderValues("Resent-Date")
        .hasSize(2)
        .allSatisfy(
            date -> Assertions.assertThat(date).isStrictlyBetween(now().minusMinutes(1), now()));
  }
}
