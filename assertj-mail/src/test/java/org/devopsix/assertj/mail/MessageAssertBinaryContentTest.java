package org.devopsix.assertj.mail;

import static org.devopsix.assertj.mail.MessageCreator.newMessage;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Test;

public class MessageAssertBinaryContentTest {

  @Test
  public void contentCannotBeExtracted() throws Exception {
    Message message = mock(Message.class);
    when(message.getDataHandler()).thenThrow(new MessagingException("error decoding content"));
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).binaryContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsMissing() throws Exception {
    DataHandler dataHandler = mock(DataHandler.class);
    when(dataHandler.getInputStream()).thenReturn(null);
    Message message = mock(Message.class);
    when(message.getDataHandler()).thenReturn(dataHandler);
    assertThrows(AssertionError.class,
        () -> MailAssertions.assertThat(message).binaryContent().satisfies((f) -> {
        }));
  }

  @Test
  public void contentIsPresent() throws Exception {
    DataHandler dataHandler = mock(DataHandler.class);
    when(dataHandler.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[] {1, 2, 3}));
    Message message = mock(Message.class);
    when(message.getDataHandler()).thenReturn(dataHandler);
    MailAssertions.assertThat(message).binaryContent().isEqualTo(new byte[] {1, 2, 3});
  }

  @Test
  public void textContentIsPresent() {
    Message message = newMessage().text("Lorem ipsum").create();
    MailAssertions.assertThat(message).binaryContent().isNotEmpty();
  }
}
