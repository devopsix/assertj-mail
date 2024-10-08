package org.devopsix.assertj.mail;

import java.util.Objects;
import javax.mail.internet.InternetAddress;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.Assertions;

/**
 * Assertions for {@link InternetAddress}.
 */
public final class InternetAddressAssert
    extends AbstractAssert<InternetAddressAssert, InternetAddress> {

  /**
   * <p>Creates a new instance.</p>
   *
   * @param internetAddress Assertion subject
   */
  public InternetAddressAssert(InternetAddress internetAddress) {
    super(internetAddress, InternetAddressAssert.class);
  }

  /**
   * <p>Returns a {@code String} assertion for the internet address' actual address part.
   * I.e. “anna@example.com” in {@code "Anna <anna@example.com>"}</p>
   *
   * @return An assertion for the address part
   */
  public AbstractCharSequenceAssert<?, String> address() {
    return Assertions.assertThat(Objects.isNull(actual) ? null : actual.getAddress());
  }

  /**
   * <p>Returns a {@code String} assertion for the internet address' personal name part.
   * I.e. “Anna” in {@code "Anna <anna@example.com>"}.</p>
   *
   * @return An assertion for the name part
   */
  public AbstractCharSequenceAssert<?, String> personal() {
    return Assertions.assertThat(Objects.isNull(actual) ? null : actual.getPersonal());
  }
}
