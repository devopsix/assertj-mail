package org.devopsix.assertj.mail;

import static java.util.Objects.nonNull;

import java.util.List;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

public class MultipartCreator {

  private String subtype = "mixed";
  private List<BodyPart> bodyParts;

  private MultipartCreator() {
  }

  public static MultipartCreator newMultipart() {
    return new MultipartCreator();
  }

  public MultipartCreator subtype(String subtype) {
    this.subtype = subtype;
    return this;
  }

  public MultipartCreator bodyParts(BodyPart... bodyParts) {
    this.bodyParts = List.of(bodyParts);
    return this;
  }

  public Multipart create() {
    try {
      MimeMultipart multipart = new MimeMultipart(subtype);
      if (nonNull(bodyParts)) {
        for (BodyPart bodyPart : bodyParts) {
          multipart.addBodyPart(bodyPart);
        }
      }
      return multipart;
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}
