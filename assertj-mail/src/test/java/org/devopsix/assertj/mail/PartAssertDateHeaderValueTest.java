package org.devopsix.assertj.mail;

import static java.time.OffsetDateTime.now;
import static org.devopsix.assertj.mail.PartCreator.newPart;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import javax.mail.MessagingException;
import javax.mail.Part;
import org.junit.jupiter.api.Test;

public class PartAssertDateHeaderValueTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Part part = mock(Part.class);
    when(part.getHeader(eq("Resent-Date"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(part)
        .dateHeaderValue("Resent-Date").satisfies((f) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Part part = newPart()
        .header("Resent-Date", now())
        .header("Resent-Date", now())
        .create();
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(part)
        .dateHeaderValue("Resent-Date").satisfies((f) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Part part = newPart().create();
    MailAssertions.assertThat(part).dateHeaderValue("Resent-Date").isNull();
  }

  @Test
  public void headerIsPresent() {
    OffsetDateTime date = now();
    Part part = newPart().header("Resent-Date", date).create();
    MailAssertions.assertThat(part).dateHeaderValue("Resent-Date").isEqualToIgnoringNanos(date);
  }
}
