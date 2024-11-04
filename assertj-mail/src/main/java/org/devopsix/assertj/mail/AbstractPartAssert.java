package org.devopsix.assertj.mail;

import java.time.OffsetDateTime;
import javax.mail.Multipart;
import javax.mail.Part;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractByteArrayAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractObjectArrayAssert;
import org.assertj.core.api.AbstractOffsetDateTimeAssert;
import org.assertj.core.api.Assertions;

abstract class AbstractPartAssert<S extends AbstractPartAssert<S, A>, A extends Part>
    extends AbstractAssert<S, A> {

  protected final PartReader partReader;

  protected AbstractPartAssert(A part, Class<? extends AbstractPartAssert<S, A>> selfType) {
    super(part, selfType);
    partReader = new PartReader(part);
  }

  /**
   * Returns a {@code String} assertion for the message part's header with the given name.
   *
   * @param name Header name
   * @return An assertion for the message part's given header
   * @throws AssertionError If there is more than 1 header with this name
   */
  public AbstractCharSequenceAssert<?, String> headerValue(String name) {
    return Assertions.assertThat(partReader.readHeader(name));
  }

  /**
   * Returns a {@code String} array assertion for the message part's header with the given
   * name.
   *
   * @param name Header name
   * @return An assertion for the message part's given header
   */
  public AbstractObjectArrayAssert<?, String> headerValues(String name) {
    return Assertions.assertThat(partReader.readHeaders(name));
  }

  /**
   * Returns a {@code OffsetDateTime} assertion for the message part's header with the given
   * name.
   *
   * @param name Header name
   * @return An assertion for the message part's given header
   * @throws AssertionError If there is more than 1 header with this name or parsing the header
   *     value fails
   */
  public AbstractOffsetDateTimeAssert<?> dateHeaderValue(String name) {
    return Assertions.assertThat(partReader.readDateHeader(name));
  }

  /**
   * Returns a {@code OffsetDateTime} array assertion for the message part's header with the
   * given name.
   *
   * @param name Header name
   * @return An assertion for the message part's given header
   * @throws AssertionError If parsing the header value fails
   */
  public AbstractObjectArrayAssert<?, OffsetDateTime> dateHeaderValues(String name) {
    return Assertions.assertThat(partReader.readDateHeaders(name));
  }

  /**
   * Returns a {@code String} assertion for the message part's plain text content.
   *
   * @return An assertion for the message part's text content
   * @throws AssertionError If the content is not plain text or reading it fails
   */
  public AbstractCharSequenceAssert<?, String> textContent() {
    return Assertions.assertThat(partReader.readTextContent());
  }

  /**
   * Returns a {@code byte[]} assertion for the message part's binary content.
   *
   * @return An assertion for the message part's binary content
   * @throws AssertionError If the content is not binary content or reading it fails
   */
  public AbstractByteArrayAssert<?> binaryContent() {
    return Assertions.assertThat(partReader.readBinaryContent());
  }

  /**
   * Asserts that the message part has multipart content ({@code Content-Type: multipart/*}).
   *
   * @return The original assertion for further chaining
   * @throws AssertionError If the content is not multipart content or reading it fails
   */
  public S isMultipart() {
    if (!partReader.isMultipart()) {
      failWithMessage("Part has no multipart content");
    }
    return myself;
  }

  /**
   * Returns a {@code Multipart} assertion for the message part's multipart content
   * ({@code Content-Type: multipart/*}).
   *
   * @return An assertion for the message part's multipart content
   * @throws AssertionError If the content is not multipart content or reading it fails
   */
  public MultipartAssert multipartContent() {
    return new MultipartAssert(partReader.readMultipartContent());
  }

  /**
   * Returns a {@code Multipart} array assertion for all the message part's multipart contents
   * ({@code Content-Type: multipart/*}). Multipart contents are searched recursively.
   *
   * <p>Please note that no assertion error occurs if the message part is not of multipart type.
   * The returned assertion contains an empty array in this case.</p>
   *
   * @return An assertion for the message part's multipart contents
   * @throws AssertionError If reading the content fails
   */
  public AbstractObjectArrayAssert<?, Multipart> multipartContents() {
    return Assertions.assertThat(partReader.readMultipartContentRecursive());
  }
}
