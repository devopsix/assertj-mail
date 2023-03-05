package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.PartCreator.newPart;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.MessagingException;
import javax.mail.Part;
import org.junit.jupiter.api.Test;

public class PartAssertTextContentTest {

  @Test
  public void contentCannotBeExtracted() throws Exception {
    Part part = mock(Part.class);
    when(part.getContent()).thenThrow(new MessagingException("error decoding content"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(part).textContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsMissing() {
    Part part = newPart().create();
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(part).textContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsPresent() {
    Part part = newPart().text("Lorem ipsum").create();
    MailAssertions.assertThat(part).textContent().isEqualTo("Lorem ipsum");
  }
}
