package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MultipartCreator.newMultipart;
import static org.devopsix.assertj.mail.PartCreator.newPart;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import org.junit.jupiter.api.Test;

public class PartAssertMultipartContentsTest {

  @Test
  public void contentCannotBeExtracted() throws Exception {
    Part part = mock(Part.class);
    when(part.getContent()).thenThrow(new MessagingException("error decoding content"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(part).multipartContents().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsPresent() {
    BodyPart part = newPart().text("Lorem ipsum").create();
    Multipart multipartInner = newMultipart().subtype("mixed").bodyParts(part).create();
    Multipart multipartOuter = newMultipart().subtype("mixed")
        .bodyParts(newPart().multipart(multipartInner).create()).create();
    Part outerPart = newPart().multipart(multipartOuter).create();
    MailAssertions.assertThat(outerPart).multipartContents().hasSize(2);
  }
}
