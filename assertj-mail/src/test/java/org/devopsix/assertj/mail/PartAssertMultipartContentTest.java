package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MultipartCreator.newMultipart;
import static org.devopsix.assertj.mail.PartCreator.newPart;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import org.junit.jupiter.api.Test;

public class PartAssertMultipartContentTest {

  @Test
  public void contentCannotBeExtracted() throws Exception {
    Part part = mock(Part.class);
    when(part.getDataHandler()).thenThrow(new MessagingException("error decoding content"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(part).multipartContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsMissing() throws Exception {
    DataHandler dataHandler = mock(DataHandler.class);
    when(dataHandler.getInputStream()).thenReturn(null);
    Part part = mock(Part.class);
    when(part.getDataHandler()).thenReturn(dataHandler);
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(part).multipartContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsPresent() {
    BodyPart innerPart = newPart().text("Lorem ipsum").create();
    Multipart multipart = newMultipart().bodyParts(innerPart).create();
    Part outerPart = newPart().multipart(multipart).create();
    MailAssertions.assertThat(outerPart).multipartContent().satisfies((f) -> {
    });
  }
}
