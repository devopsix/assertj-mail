package org.devopsix.assertj.mail;

import java.util.LinkedList;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;

class MultipartReader {

  private final Multipart multipart;

  MultipartReader(Multipart multipart) {
    this.multipart = multipart;
  }

  String readContentType() {
    return multipart.getContentType();
  }

  BodyPart[] readParts() {
    try {
      LinkedList<BodyPart> parts = new LinkedList<>();
      for (int i = 0; i < multipart.getCount(); i++) {
        parts.add(multipart.getBodyPart(i));
      }
      return parts.toArray(new BodyPart[0]);
    } catch (MessagingException e) {
      throw new AssertionError("Failed to extract parts", e);
    }
  }
}
