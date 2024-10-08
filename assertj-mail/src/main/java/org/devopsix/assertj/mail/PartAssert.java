package org.devopsix.assertj.mail;

import javax.mail.Part;

/**
 * Assertions for {@link Part}.
 */
public final class PartAssert extends AbstractPartAssert<PartAssert, Part> {

  /**
   * <p>Creates a new instance.</p>
   *
   * @param part Assertion subject
   */
  public PartAssert(Part part) {
    super(part, PartAssert.class);
  }
}
