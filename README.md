[![Build Status](https://img.shields.io/github/actions/workflow/status/devopsix/assertj-mail/build.yml)](https://github.com/devopsix/assertj-mail/actions?query=workflow%3ABuild)
[![Maven Central](https://img.shields.io/maven-central/v/org.devopsix/assertj-mail.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.devopsix%22%20AND%20a:%assertj-mail%22)
[![License](https://img.shields.io/github/license/devopsix/assertj-mail)](LICENSE.txt)

# AssertJ Mail

AssertJ Mail is an extension library for the [AssertJ][] assertion library.
It provides assertions for types from the `javax.mail` and `jakarta.mail` packages.

The [hamcrest-mail][] sister project provides a set of Hamcrest matchers with similar features.

## Usage
To use AssertJ Mail in a Maven project add a dependency on `org.devopsix:assertj-mail` (for Java EE 8 / javax.mail) or
`org.devopsix:assertj-mail-jakarta` (for Jakarta EE 9+ / jakarta.mail) to the pom.xml file.

```xml
<!-- Maven coordinates for Java EE 8 / javax.mail -->
<dependency>
    <groupId>org.devopsix</groupId>
    <artifactId>assertj-mail</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
<!-- Maven coordinates for Jakarta EE 9+ / jakarta.mail -->
<dependency>
    <groupId>org.devopsix</groupId>
    <artifactId>assertj-mail-jakarta</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

The assertions are available as static methods on the `MailAssertions` class.

Here are a few examples:

```java
Message message;
MailAssertions.assertThat(message).from()
    .singleElement(as(MailAssertions.INTERNET_ADDRESS))
    .address().isEqualTo("anna@example.com");

MailAssertions.assertThat(message).to()
    .singleElement(Assertions.as(MailAssertions.INTERNET_ADDRESS))
    .address().isEqualTo("anna@example.com");

MailAssertions.assertThat(message).subject().isEqualTo("Foo");

MailAssertions.assertThat(message).headerValue("Message-ID").isEqualTo("Foo");

MailAssertions.assertThat(message).dateHeaderValue("Resent-Date").isEqualToIgnoringNanos(date);
```

More example can be found in the [examples](examples/) directory (for Java EE 8 / javax.mail) and in the
[examples-jakarta](examples-jakarta/) directory (for Jakarta EE 9+ / jakarta.mail).

## Assertions

* `MessageAssert.headerValue(String name)` - An assertion for a message's header (String value)
* `MessageAssert.headerValues(String name)` - An assertion for a message's header (multiple Strings values)
* `MessageAssert.dateHeaderValue(String name)` - An assertion for a message's header (date value)
* `MessageAssert.dateHeaderValues(String name)` - An assertion for a message's header (multiple date values)
* `MessageAssert.textContent()` - An assertion for a message's text content
* `MessageAssert.binaryContent()` - An assertion for a message's binary content
* `MessageAssert.isMultipart()` - Asserts that a message has multipart content
* `MessageAssert.multipartContent()` - An assertion for a message's multipart content
* `MessageAssert.multipartContents()` - An assertion for a message's multipart contents (recursive)
* `MessageAssert.date()` - An assertion for a message's “Date” header
* `MessageAssert.from()` - An assertion for a message's “From” header
* `MessageAssert.sender()` - An assertion for a message's “Sender” header
* `MessageAssert.replyTo()` - An assertion for a message's “Reply-To” header
* `MessageAssert.to()` - An assertion for a message's “To” header
* `MessageAssert.cc()` - An assertion for a message's “Cc” header
* `MessageAssert.bcc()` - An assertion for a message's “Bcc” header
* `MessageAssert.subject()` - An assertion for a message's “Subject” header
* `PartAssert.dkimSignature(Map<String, String> publicKeys)` - An assertion for the part's DKIM signature
* `PartAssert.headerValue(String name)` - An assertion for a part's header (String value)
* `PartAssert.headerValues(String name)` - An assertion for a part's header (multiple Strings values)
* `PartAssert.dateHeaderValue(String name)` - An assertion a part's header (date value)
* `PartAssert.dateHeaderValues(String name)` - An assertion for a part's header (multiple date values)
* `PartAssert.textContent()` - An assertion for a part's text content
* `PartAssert.binaryContent()` - An assertion for a part's binary content
* `PartAssert.isMultipart()` - Asserts that a part has multipart content
* `PartAssert.multipartContent()` - An assertion for a part's multipart content
* `PartAssert.multipartContents()` - An assertion for a part's multipart contents (recursive)
* `DkimSignatureAssert.isValid()` - Asserts that a DKIM signature is valid
* `DkimSignatureAssert.records()` - An assertion for the signature records of a [DKIM][] signature
* `MultipartAssert.contentType()` - An assertion for a multipart's content type
* `MultipartAssert.parts()` - An assertion for a multipart's actual parts
* `InternetAddressAssert.address()` - An assertion for an address' address part
* `InternetAddressAssert.personal()` - An assertion for an address' name part

[AssertJ]: https://github.com/assertj/assertj
[DKIM]: https://tools.ietf.org/html/rfc4871
[hamcrest-mail]: https://github.com/devopsix/hamcrest-mail
