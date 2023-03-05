package org.devopsix.assertj.mail;

import static java.util.Collections.singletonList;
import static org.devopsix.assertj.mail.MessageCreator.newMessage;

import javax.mail.Message;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageAssertFactoryTest {

  @Test
  public void useFactorySingleMessage() {
    Message message = newMessage().create();
    Assertions.assertThat(message).asInstanceOf(MailAssertions.MESSAGE).isNotNull();
  }

  @Test
  public void useFactoryWithList() {
    Message message = newMessage().create();
    Assertions.assertThat(singletonList(message)).singleElement(MailAssertions.MESSAGE).isNotNull();
  }
}
