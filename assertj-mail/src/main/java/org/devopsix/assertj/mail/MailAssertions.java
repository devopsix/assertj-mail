package org.devopsix.assertj.mail;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactory;

/**
 * This class provides access to all AssertJ Mail assertions.
 */
public final class MailAssertions {

  private MailAssertions() {
  }

  /**
   * A factory for creating instances of {@link MessageAssert}.
   *
   * @see Assertions#as(InstanceOfAssertFactory)
   */
  public static final InstanceOfAssertFactory<Message, MessageAssert> MESSAGE =
      new InstanceOfAssertFactory<>(Message.class, MailAssertions::assertThat);

  /**
   * A factory for creating instances of {@link PartAssert}.
   *
   * @see Assertions#as(InstanceOfAssertFactory)
   */
  public static final InstanceOfAssertFactory<Part, PartAssert> PART =
      new InstanceOfAssertFactory<>(Part.class, MailAssertions::assertThat);

  /**
   * A factory for creating instances of {@link MultipartAssert}.
   *
   * @see Assertions#as(InstanceOfAssertFactory)
   */
  public static final InstanceOfAssertFactory<Multipart, MultipartAssert> MULTIPART =
      new InstanceOfAssertFactory<>(Multipart.class, MailAssertions::assertThat);

  /**
   * A factory for creating instances of {@link InternetAddressAssert}.
   *
   * @see Assertions#as(InstanceOfAssertFactory)
   */
  public static final InstanceOfAssertFactory<InternetAddress, InternetAddressAssert>
      INTERNET_ADDRESS =
      new InstanceOfAssertFactory<>(InternetAddress.class, MailAssertions::assertThat);

  /**
   * Returns an instance of {@code MessageAssert} which can be used to verify assertions
   * for the given message.
   *
   * @param message The message to assert
   * @return An instance of {@code MessageAssert} for the given message
   */
  public static MessageAssert assertThat(Message message) {
    return new MessageAssert(message);
  }

  /**
   * Returns an instance of {@code PartAssert} which can be used to verify assertions
   * for the given message part.
   *
   * @param part The message part to assert
   * @return An instance of {@code PartAssert} for the given message
   */
  public static PartAssert assertThat(Part part) {
    return new PartAssert(part);
  }

  /**
   * Returns an instance of {@code MultipartAssert} which can be used to verify assertions
   * for the given multipart.
   *
   * @param multipart The multipart to assert
   * @return An instance of {@code MultipartAssert} for the given multipart
   */
  public static MultipartAssert assertThat(Multipart multipart) {
    return new MultipartAssert(multipart);
  }

  /**
   * Returns an instance of {@code InternetAddressAssert} which can be used to verify assertions
   * for the given internet address.
   *
   * @param address The internet address to assert
   * @return An instance of {@code InternetAddressAssert} for the given message
   */
  public static InternetAddressAssert assertThat(InternetAddress address) {
    return new InternetAddressAssert(address);
  }
}
