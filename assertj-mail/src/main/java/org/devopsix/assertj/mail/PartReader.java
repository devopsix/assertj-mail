package org.devopsix.assertj.mail;

import static java.lang.String.format;
import static java.time.OffsetDateTime.parse;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static org.devopsix.assertj.mail.ArrayUtils.isEmpty;
import static org.devopsix.assertj.mail.HeaderUtils.decodeHeader;
import static org.devopsix.assertj.mail.IoUtils.toByteArray;
import static org.devopsix.assertj.mail.MailDateTimeFormatter.MAIL_DATE_TIME;
import static org.devopsix.assertj.mail.MailDateTimeFormatter.trimTrailingZoneText;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Function;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

final class PartReader {

  private final Part part;

  PartReader(Part part) {
    this.part = part;
  }

  OffsetDateTime readDateHeader(String name) {
    return readDateHeader(part, name);
  }

  <P extends Part> OffsetDateTime readDateHeader(P part, String name) {
    return readHeader(part, name, this::parseDate);
  }

  OffsetDateTime[] readDateHeaders(String name) {
    return readDateHeaders(part, name);
  }

  <P extends Part> OffsetDateTime[] readDateHeaders(P part, String name) {
    String[] values = readHeaders(part, name);
    if (isNull(values)) {
      return null;
    }
    return stream(values)
        .map(this::parseDate)
        .toArray(OffsetDateTime[]::new);
  }

  InternetAddress readAddressHeader(String name) {
    return readAddressHeader(part, name);
  }

  <P extends Part> InternetAddress readAddressHeader(P part, String name) {
    return readHeader(part, name, this::parseAddress);
  }

  InternetAddress[] readAddressListHeader(String name) {
    return readAddressListHeader(part, name);
  }

  <P extends Part> InternetAddress[] readAddressListHeader(P part, String name) {
    return readHeader(part, name, this::parseAddresses);
  }

  String readHeader(String name) {
    return readHeader(part, name);
  }

  private <P extends Part, T> T readHeader(P part, String name, Function<String, T> factory) {
    String value = readHeader(part, name);
    if (isNull(value)) {
      return null;
    }
    return factory.apply(value);
  }

  <P extends Part> String readHeader(P part, String name) {
    String[] values;
    try {
      values = part.getHeader(name);
    } catch (MessagingException e) {
      throw new AssertionError(e);
    }
    if (isEmpty(values)) {
      return null;
    }
    if (values.length > 1) {
      throw new AssertionError(format("Found more than one “%s” header", name));
    }
    return decodeHeader(values[0]);
  }

  String[] readHeaders(String name) {
    return readHeaders(part, name);
  }

  <P extends Part> String[] readHeaders(P part, String name) {
    String[] values;
    try {
      values = part.getHeader(name);
    } catch (MessagingException e) {
      throw new AssertionError(e);
    }
    if (isEmpty(values)) {
      return null;
    }
    return stream(values)
        .map(HeaderUtils::decodeHeader)
        .toArray(String[]::new);
  }

  private OffsetDateTime parseDate(String value) {
    try {
      value = trimTrailingZoneText(value);
      return parse(value, MAIL_DATE_TIME);
    } catch (DateTimeParseException e) {
      throw new AssertionError(
          format("Failed to parse date header value %s: %s", value, e.getMessage()));
    }
  }

  private InternetAddress parseAddress(String value) {
    try {
      return new InternetAddress(value);
    } catch (AddressException e) {
      throw new AssertionError(
          format("Failed to parse address header value %s: %s", value, e.getMessage()));
    }
  }

  private InternetAddress[] parseAddresses(String value) {
    try {
      return InternetAddress.parse(value);
    } catch (AddressException e) {
      throw new AssertionError(
          format("Failed to parse address list header value %s: %s", value, e.getMessage()));
    }
  }

  InputStream toInputStream(Part part) throws IOException, MessagingException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    part.writeTo(buffer);
    return new ByteArrayInputStream(buffer.toByteArray());
  }

  String readTextContent() {
    Object content;
    try {
      content = part.getContent();
    } catch (IOException | MessagingException e) {
      throw new AssertionError(e);
    }
    if (content instanceof String) {
      return (String) content;
    } else {
      throw new AssertionError(format("Is not text content: %s",
          isNull(content) ? null : content.getClass().getSimpleName()));
    }
  }

  byte[] readBinaryContent() {
    try {
      InputStream data = part.getDataHandler().getInputStream();
      if (isNull(data)) {
        throw new AssertionError("Is not binary content");
      } else {
        return toByteArray(data);
      }
    } catch (IOException | MessagingException e) {
      throw new AssertionError("Failed to extract content", e);
    }
  }

  boolean isMultipart() {
    try {
      return part.getContent() instanceof Multipart;
    } catch (IOException | MessagingException e) {
      throw new AssertionError("Failed to extract content", e);
    }
  }

  Multipart readMultipartContent() {
    try {
      Object content = part.getContent();
      if (content instanceof Multipart) {
        return (Multipart) content;
      } else {
        throw new AssertionError("Is not multipart content");
      }
    } catch (IOException | MessagingException e) {
      throw new AssertionError("Failed to extract content", e);
    }
  }

  Multipart[] readMultipartContentRecursive() {
    return multiparts(part).toArray(new Multipart[0]);
  }

  private Collection<Multipart> multiparts(Part part) {
    LinkedList<Multipart> multiparts = new LinkedList<>();
    try {
      Object content = part.getContent();
      if (content instanceof Multipart) {
        Multipart multipart = (Multipart) content;
        multiparts.add(multipart);
        for (int i = 0; i < multipart.getCount(); i++) {
          multiparts.addAll(multiparts(multipart.getBodyPart(i)));
        }
      }
    } catch (IOException | MessagingException e) {
      throw new AssertionError("Failed to extract content", e);
    }
    return multiparts;
  }
}
