package org.devopsix.assertj.mail;

import static java.util.Objects.isNull;

final class ArrayUtils {

  private ArrayUtils() {
  }

  static boolean isEmpty(String[] array) {
    return isNull(array) || array.length == 0;
  }
}
