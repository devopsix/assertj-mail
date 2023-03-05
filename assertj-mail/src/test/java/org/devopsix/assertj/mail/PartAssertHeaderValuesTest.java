package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.PartCreator.newPart;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.MessagingException;
import javax.mail.Part;
import org.junit.jupiter.api.Test;

public class PartAssertHeaderValuesTest {

  @Test
  public void headerCannotBeExtracted() throws Exception {
    Part part = mock(Part.class);
    when(part.getHeader(eq("Custom-Header"))).thenThrow(
        new MessagingException("error decoding header"));
    assertThrows(AssertionError.class, () -> MailAssertions.assertThat(part)
        .headerValues("Custom-Header").anySatisfy((f) -> {
        }));
  }

  @Test
  public void headerIsMissing() {
    Part part = newPart().create();
    MailAssertions.assertThat(part).headerValues("Custom-Header").isNull();
  }

  @Test
  public void headersArePresent() {
    Part part = newPart()
        .header("Custom-Header", "Foo")
        .header("Custom-Header", "Bar")
        .create();
    MailAssertions.assertThat(part).headerValues("Custom-Header")
        .hasSize(2)
        .contains("Foo", "Bar");
  }
}
