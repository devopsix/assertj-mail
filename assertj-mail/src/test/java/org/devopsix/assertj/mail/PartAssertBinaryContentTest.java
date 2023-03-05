package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.PartCreator.newPart;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Part;
import org.junit.jupiter.api.Test;

public class PartAssertBinaryContentTest {

  @Test
  public void contentCannotBeExtracted() throws Exception {
    Part part = mock(Part.class);
    when(part.getDataHandler()).thenThrow(new MessagingException("error decoding content"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(part).binaryContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsMissing() throws Exception {
    DataHandler dataHandler = mock(DataHandler.class);
    when(dataHandler.getInputStream()).thenReturn(null);
    Part part = mock(Part.class);
    when(part.getDataHandler()).thenReturn(dataHandler);
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(part).binaryContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsPresent() throws Exception {
    DataHandler dataHandler = mock(DataHandler.class);
    when(dataHandler.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {1, 2, 3}));
    Part part = mock(Part.class);
    when(part.getDataHandler()).thenReturn(dataHandler);
    MailAssertions.assertThat(part).binaryContent().isEqualTo(new byte[] {1, 2, 3});
  }

  @Test
  public void textContentIsPresent() {
    Part part = newPart().text("Lorem ipsum").create();
    MailAssertions.assertThat(part).binaryContent().isNotEmpty();
  }
}
