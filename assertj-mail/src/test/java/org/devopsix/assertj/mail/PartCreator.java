package org.devopsix.assertj.mail;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Objects.nonNull;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;

public class PartCreator {

  private static final class Header {

    private final String name;
    private final String value;

    public Header(String name, String value) {
      this.name = name;
      this.value = value;
    }
  }

  private final List<Header> headers = new LinkedList<>();
  private String text;
  private Multipart multipart;
  private byte[] content;

  private PartCreator() {
  }

  public static PartCreator newPart() {
    return new PartCreator();
  }

  public PartCreator header(String name, String value) {
    this.headers.add(new Header(name, value));
    return this;
  }

  public PartCreator header(String name, OffsetDateTime value) {
    header(name, value.format(RFC_1123_DATE_TIME));
    return this;
  }

  public PartCreator text(String text) {
    clearAllContent();
    this.text = text;
    return this;
  }

  public PartCreator multipart(Multipart multipart) {
    clearAllContent();
    this.multipart = multipart;
    return this;
  }

  public PartCreator content(byte[] content) {
    clearAllContent();
    this.content = content;
    return this;
  }

  private void clearAllContent() {
    this.text = null;
    this.multipart = null;
    this.content = null;
  }

  public MimeBodyPart create() {
    try {
      if (nonNull(content)) {
        InternetHeaders headers = new InternetHeaders();
        for (Header header : this.headers) {
          headers.addHeader(header.name, header.value);
        }
        return new MimeBodyPart(headers, content);
      } else {
        MimeBodyPart part = new MimeBodyPart();
        for (Header header : headers) {
          part.addHeader(header.name, header.value);
        }
        if (nonNull(text)) {
          part.setText(text);
        }
        if (nonNull(multipart)) {
          part.setContent(multipart);
        }
        return part;
      }
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }
}
