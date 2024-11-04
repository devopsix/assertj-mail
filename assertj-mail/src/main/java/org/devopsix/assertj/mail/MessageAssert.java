package org.devopsix.assertj.mail;

import static java.util.Objects.requireNonNull;
import static org.devopsix.assertj.mail.HeaderNames.BCC;
import static org.devopsix.assertj.mail.HeaderNames.CC;
import static org.devopsix.assertj.mail.HeaderNames.DATE;
import static org.devopsix.assertj.mail.HeaderNames.FROM;
import static org.devopsix.assertj.mail.HeaderNames.REPLY_TO;
import static org.devopsix.assertj.mail.HeaderNames.SENDER;
import static org.devopsix.assertj.mail.HeaderNames.SUBJECT;
import static org.devopsix.assertj.mail.HeaderNames.TO;

import java.util.Map;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractObjectArrayAssert;
import org.assertj.core.api.AbstractOffsetDateTimeAssert;
import org.assertj.core.api.Assertions;

/**
 * Assertiosn for {@link Message}.
 */
public final class MessageAssert extends AbstractPartAssert<MessageAssert, Message> {

  /**
   * Creates a new instance.
   *
   * @param message Assertion subject
   */
  public MessageAssert(Message message) {
    super(message, MessageAssert.class);
  }

  /**
   * Returns a {@code OffsetDateTime} assertion for the message's “Date” header.
   *
   * @return An assertion for the message's “Date” header
   * @throws AssertionError If there is more than one “Date” header or parsing the header value
   *     fails
   */
  public AbstractOffsetDateTimeAssert<?> date() {
    return Assertions.assertThat(partReader.readDateHeader(DATE));
  }

  /**
   * Returns an {@code InternetAddress} array assertion for the message's “From” header.
   *
   * @return An assertion for the message's “From” header
   * @throws AssertionError If there is more than one “From” header or parsing the header value
   *     fails
   */
  public AbstractObjectArrayAssert<?, InternetAddress> from() {
    return Assertions.assertThat(partReader.readAddressListHeader(FROM));
  }

  /**
   * Returns an {@code InternetAddress} assertion for the message's “Sender” header.
   *
   * @return An assertion for the message's “Sender” header
   * @throws AssertionError If there is more than one “Sender” header or parsing the header value
   *     fails
   */
  public InternetAddressAssert sender() {
    return new InternetAddressAssert(partReader.readAddressHeader(SENDER));
  }

  /**
   * Returns an {@code InternetAddress} array assertion for the message's “Reply-To” header.
   *
   * @return An assertion for the message's “Reply-To” header
   * @throws AssertionError If there is more than one “Reply-To” header or parsing the header value
   *     fails
   */
  public AbstractObjectArrayAssert<?, InternetAddress> replyTo() {
    return Assertions.assertThat(partReader.readAddressListHeader(REPLY_TO));
  }

  /**
   * Returns an {@code InternetAddress} array assertion for the message's “To” header.
   *
   * @return An assertion for the message's “To” header
   * @throws AssertionError If there is more than one “To” header or parsing the header value fails
   */
  public AbstractObjectArrayAssert<?, InternetAddress> to() {
    return Assertions.assertThat(partReader.readAddressListHeader(TO));
  }

  /**
   * Returns an {@code InternetAddress} array assertion for the message's “Cc” header.
   *
   * @return An assertion for the message's “Cc” header
   * @throws AssertionError If there is more than one “Cc” header or parsing the header value fails
   */
  public AbstractObjectArrayAssert<?, InternetAddress> cc() {
    return Assertions.assertThat(partReader.readAddressListHeader(CC));
  }

  /**
   * Returns an {@code InternetAddress} array assertion for the message's “Bcc” header.
   *
   * @return An assertion for the message's “Bcc” header
   * @throws AssertionError If there is more than one “Bcc” header or parsing the header value fails
   */
  public AbstractObjectArrayAssert<?, InternetAddress> bcc() {
    return Assertions.assertThat(partReader.readAddressListHeader(BCC));
  }

  /**
   * Returns a {@code String} assertion for the message's “Subject” header.
   *
   * @return An assertion for the message's “Subject” header
   * @throws AssertionError If there is more than one “Subject” header
   */
  public AbstractCharSequenceAssert<?, String> subject() {
    return Assertions.assertThat(partReader.readHeader(SUBJECT));
  }

  /**
   * Returns a {@code DkimSignatureAssert} assertion for the message's DKIM signature.
   *
   * <p>DKIM public keys are distributed as DNS TXT records. As tests should not depend
   * on any real DNS records this method accepts a map of virtual TXT records. The map keys
   * are fully qualified DNS domain names (without trailing dot) and the map values are
   * DKIM public keys. The public keys must be represented as described in
   * <a href="https://tools.ietf.org/html/rfc4871#section-3.6">section 3.6. of RFC 4871</a>.</p>
   *
   * <p>Example: &quot;foo._domainkey.example.com&quot; =&gt; &quot;k=rsa; p=Abcd1234&quot;</p>
   *
   * @param publicKeys Map with public keys
   * @return An assertion for the message's DKIM signature
   * @throws NullPointerException when {@code publicKeys} is {@code null}
   */
  public DkimSignatureAssert dkimSignature(Map<String, String> publicKeys) {
    requireNonNull(publicKeys);
    return DkimSignatureAssert.assertThat(actual, publicKeys);
  }
}
