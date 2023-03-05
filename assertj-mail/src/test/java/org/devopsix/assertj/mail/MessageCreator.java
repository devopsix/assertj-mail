package org.devopsix.assertj.mail;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;
import static java.util.Collections.singleton;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.james.jdkim.DKIMSigner;
import org.apache.james.jdkim.exceptions.FailException;

public class MessageCreator {

  private static final class Header {

    private final String name;
    private final String value;

    public Header(String name, String value) {
      this.name = name;
      this.value = value;
    }
  }

  private OffsetDateTime date;
  private Collection<String> from;
  private String sender;
  private Collection<String> replyTo;
  private Collection<String> to;
  private Collection<String> cc;
  private Collection<String> bcc;
  private String subject;
  private final List<Header> headers = new LinkedList<>();
  private String text = "";
  private Multipart multipart;
  private KeyPair dkimSignatureKeyPair;
  private String dkimSignatureDomain;
  private String dkimSignatureSelector;
  private Collection<String> dkimSignatureHeaders;

  private MessageCreator() {
  }

  public static MessageCreator newMessage() {
    return new MessageCreator();
  }

  public MessageCreator date(OffsetDateTime date) {
    this.date = date;
    return this;
  }

  public MessageCreator from(String from) {
    this.from = singleton(from);
    return this;
  }

  public MessageCreator from(Collection<String> from) {
    this.from = from;
    return this;
  }

  public MessageCreator sender(String sender) {
    this.sender = sender;
    return this;
  }

  public MessageCreator replyTo(String replyTo) {
    this.replyTo = singleton(replyTo);
    return this;
  }

  public MessageCreator replyTo(Collection<String> replyTo) {
    this.replyTo = replyTo;
    return this;
  }

  public MessageCreator to(String to) {
    this.to = singleton(to);
    return this;
  }

  public MessageCreator to(Collection<String> to) {
    this.to = to;
    return this;
  }

  public MessageCreator cc(String cc) {
    this.cc = singleton(cc);
    return this;
  }

  public MessageCreator cc(Collection<String> cc) {
    this.cc = cc;
    return this;
  }

  public MessageCreator bcc(String bcc) {
    this.bcc = singleton(bcc);
    return this;
  }

  public MessageCreator bcc(Collection<String> bcc) {
    this.bcc = bcc;
    return this;
  }

  public MessageCreator subject(String subject) {
    this.subject = subject;
    return this;
  }

  public MessageCreator header(String name, String value) {
    this.headers.add(new Header(name, value));
    return this;
  }

  public MessageCreator header(String name, OffsetDateTime value) {
    header(name, value.format(RFC_1123_DATE_TIME));
    return this;
  }

  public MessageCreator text(String text) {
    this.text = text;
    this.multipart = null;
    return this;
  }

  public MessageCreator multipart(Multipart multipart) {
    this.multipart = multipart;
    this.text = null;
    return this;
  }

  public MessageCreator dkimSignature(
      KeyPair keyPair,
      String domain,
      String selector,
      Collection<String> headers
  ) {
    this.dkimSignatureKeyPair = keyPair;
    this.dkimSignatureDomain = domain;
    this.dkimSignatureSelector = selector;
    this.dkimSignatureHeaders = headers;
    return this;
  }

  public MimeMessage create() {
    try {
      Session session = Session.getDefaultInstance(new Properties());
      MimeMessage message = new MimeMessage(session);
      if (nonNull(date)) {
        message.setSentDate(new Date(date.toInstant().toEpochMilli()));
      }
      if (nonNull(from)) {
        message.addFrom(addresses(from));
      }
      if (nonNull(sender)) {
        message.setSender(new InternetAddress(sender));
      }
      if (nonNull(replyTo)) {
        message.setReplyTo(addresses(replyTo));
      }
      if (nonNull(to)) {
        message.setRecipients(TO, addresses(to));
      }
      if (nonNull(cc)) {
        message.setRecipients(CC, addresses(cc));
      }
      if (nonNull(bcc)) {
        message.setRecipients(BCC, addresses(bcc));
      }
      message.setSubject(subject);
      for (Header header : headers) {
        message.addHeader(header.name, header.value);
      }
      if (nonNull(text)) {
        message.setText(text);
      }
      if (nonNull(multipart)) {
        message.setContent(multipart);
      }
      signMessage(message);
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      message.writeTo(buffer);
      return new MimeMessage(message.getSession(), new ByteArrayInputStream(buffer.toByteArray()));
    } catch (IOException | FailException | MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  private Address[] addresses(Collection<String> addresses) {
    return addresses.stream()
        .map(r -> {
          try {
            return new InternetAddress(r);
          } catch (AddressException e) {
            throw new RuntimeException(e);
          }
        })
        .toArray(Address[]::new);
  }

  private void signMessage(Message message) throws IOException, FailException, MessagingException {
    if (isNull(dkimSignatureKeyPair)
        || isNull(dkimSignatureDomain)
        || isNull(dkimSignatureSelector)
        || isNull(dkimSignatureHeaders)
    ) {
      return;
    }
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    message.writeTo(buffer);
    String signatureRecordTemplate = format("v=1; a=rsa-sha256; d=%1$s; s=%2$s; h=%3$s; b=; bh=;",
        dkimSignatureDomain, dkimSignatureSelector, String.join(":", dkimSignatureHeaders));
    DKIMSigner signer = new DKIMSigner(signatureRecordTemplate, dkimSignatureKeyPair.getPrivate());
    String dkimHeader = signer.sign(new ByteArrayInputStream(buffer.toByteArray()));
    message.setHeader("DKIM-Signature", dkimHeader.substring("DKIM-Signature:".length()).trim());
  }
}
