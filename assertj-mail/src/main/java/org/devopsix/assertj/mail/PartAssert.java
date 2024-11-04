package org.devopsix.assertj.mail;

import javax.mail.Part;

/**
 * Assertions for {@link Part}.
 */
public final class PartAssert extends AbstractPartAssert<PartAssert, Part> {

  /**
   * Creates a new instance.
   *
   * @param part Assertion subject
   */
  public PartAssert(Part part) {
    super(part, PartAssert.class);
  }
}
