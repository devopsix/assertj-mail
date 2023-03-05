package org.devopsix.assertj.mail;

import javax.mail.BodyPart;
import javax.mail.Multipart;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractObjectArrayAssert;
import org.assertj.core.api.Assertions;

/**
 * Assertions for {@link Multipart}.
 */
public final class MultipartAssert extends AbstractAssert<MultipartAssert, Multipart> {

  private final MultipartReader multipartReader;

  public MultipartAssert(Multipart multipart) {
    super(multipart, MultipartAssert.class);
    multipartReader = new MultipartReader(multipart);
  }

  /**
   * <p>Returns a {@code String} assertion for the multipart's content type.</p>
   *
   * @return An assertion for the multipart's content type
   */
  public AbstractCharSequenceAssert<?, String> contentType() {
    return Assertions.assertThat(multipartReader.readContentType());
  }

  /**
   * <p>Returns an {@code BodyPart} array assertion for the multipart's parts.</p>
   *
   * @return An assertion for the multipart's parts
   * @throws AssertionError If reading the parts fails
   */
  public AbstractObjectArrayAssert<?, BodyPart> parts() {
    return Assertions.assertThat(multipartReader.readParts());
  }
}
