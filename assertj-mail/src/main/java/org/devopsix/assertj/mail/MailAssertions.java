package org.devopsix.assertj.mail;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactory;

/**
 * <p>This class provides access to all AssertJ Mail assertions.</p>
 */
public final class MailAssertions {

  private MailAssertions() {
  }

  /**
   * <p>A factory for creating instances of {@link MessageAssert}.</p>
   *
   * @see Assertions#as(InstanceOfAssertFactory)
   */
  public static final InstanceOfAssertFactory<Message, MessageAssert> MESSAGE =
      new InstanceOfAssertFactory<>(Message.class, MailAssertions::assertThat);

  /**
   * <p>A factory for creating instances of {@link PartAssert}.</p>
   *
   * @see Assertions#as(InstanceOfAssertFactory)
   */
  public static final InstanceOfAssertFactory<Part, PartAssert> PART =
      new InstanceOfAssertFactory<>(Part.class, MailAssertions::assertThat);

  /**
   * <p>A factory for creating instances of {@link MultipartAssert}.</p>
   *
   * @see Assertions#as(InstanceOfAssertFactory)
   */
  public static final InstanceOfAssertFactory<Multipart, MultipartAssert> MULTIPART =
      new InstanceOfAssertFactory<>(Multipart.class, MailAssertions::assertThat);

  /**
   * <p>A factory for creating instances of {@link InternetAddressAssert}.</p>
   *
   * @see Assertions#as(InstanceOfAssertFactory)
   */
  public static final InstanceOfAssertFactory<InternetAddress, InternetAddressAssert>
      INTERNET_ADDRESS =
      new InstanceOfAssertFactory<>(InternetAddress.class, MailAssertions::assertThat);

  /**
   * <p>Returns an instance of {@code MessageAssert} which can be used to verify assertions
   * for the given message.</p>
   *
   * @param message The message to assert
   * @return An instance of {@code MessageAssert} for the given message
   */
  public static MessageAssert assertThat(Message message) {
    return new MessageAssert(message);
  }

  /**
   * <p>Returns an instance of {@code PartAssert} which can be used to verify assertions
   * for the given message part.</p>
   *
   * @param part The message part to assert
   * @return An instance of {@code PartAssert} for the given message
   */
  public static PartAssert assertThat(Part part) {
    return new PartAssert(part);
  }

  /**
   * <p>Returns an instance of {@code MultipartAssert} which can be used to verify assertions
   * for the given multipart.</p>
   *
   * @param multipart The multipart to assert
   * @return An instance of {@code MultipartAssert} for the given multipart
   */
  public static MultipartAssert assertThat(Multipart multipart) {
    return new MultipartAssert(multipart);
  }

  /**
   * <p>Returns an instance of {@code InternetAddressAssert} which can be used to verify assertions
   * for the given internet address.</p>
   *
   * @param address The internet address to assert
   * @return An instance of {@code InternetAddressAssert} for the given message
   */
  public static InternetAddressAssert assertThat(InternetAddress address) {
    return new InternetAddressAssert(address);
  }
}
