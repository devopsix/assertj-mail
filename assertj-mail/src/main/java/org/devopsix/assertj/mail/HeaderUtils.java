package org.devopsix.assertj.mail;

import java.io.UnsupportedEncodingException;
import javax.mail.internet.MimeUtility;

final class HeaderUtils {

  private HeaderUtils() {
  }

  static String decodeHeader(String rawValue) {
    try {
      return MimeUtility.decodeText(MimeUtility.unfold(rawValue));
    } catch (UnsupportedEncodingException ex) {
      return rawValue;
    }
  }
}
