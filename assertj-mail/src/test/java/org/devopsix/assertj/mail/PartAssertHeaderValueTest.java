package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.PartCreator.newPart;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.MessagingException;
import javax.mail.Part;
import org.junit.jupiter.api.Test;

public class PartAssertHeaderValueTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Part part = mock(Part.class);
    when(part.getHeader(eq("Message-ID"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(part)
        .headerValue("Message-ID").satisfies((f) -> {
        }));
  }

  @Test
  public void headerIsPresentTwice() {
    Part part = newPart()
        .header("Message-ID", "Foo")
        .header("Message-ID", "Bar")
        .create();
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(part)
        .headerValue("Message-ID").satisfies((f) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Part part = newPart().create();
    MailAssertions.assertThat(part).headerValue("Message-ID").isNull();
  }

  @Test
  public void headerIsPresent() {
    Part part = newPart().header("Message-ID", "Foo").create();
    MailAssertions.assertThat(part).headerValue("Message-ID").isEqualTo("Foo");
  }
}
