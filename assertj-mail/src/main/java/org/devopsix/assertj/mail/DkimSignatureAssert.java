package org.devopsix.assertj.mail;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.devopsix.assertj.mail.CollectionUtils.isEmpty;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.mail.MessagingException;
import javax.mail.Part;
import org.apache.james.jdkim.DKIMVerifier;
import org.apache.james.jdkim.api.PublicKeyRecordRetriever;
import org.apache.james.jdkim.api.SignatureRecord;
import org.apache.james.jdkim.exceptions.FailException;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;

/**
 * Assertions for DKIM signatures.
 */
public class DkimSignatureAssert extends AbstractAssert<DkimSignatureAssert, Part> {

  private final PartReader partReader;
  private final DKIMVerifier dkimVerifier;

  private DkimSignatureAssert(Part message, Map<String, String> publicKeys) {
    super(message, DkimSignatureAssert.class);
    partReader = new PartReader(message);
    dkimVerifier = new DKIMVerifier(new RecordRetriever(publicKeys));
  }

  /**
   * <p>Creates a new instance.</p>
   *
   * @param message Assertion subject
   * @param publicKeys Map of public keys as they would be published in DNS TXT records
   * @return New instance
   */
  public static DkimSignatureAssert assertThat(Part message, Map<String, String> publicKeys) {
    return new DkimSignatureAssert(message, publicKeys);
  }

  /**
   * <p>Asserts that the DKIM signature is valid.</p>
   *
   * @return The original assertion for further chaining
   * @throws AssertionError If the DKIM signature is not valid
   */
  public DkimSignatureAssert isValid() {
    try {
      List<SignatureRecord> signatures = dkimVerifier.verify(partReader.toInputStream(actual));
      if (isEmpty(signatures)) {
        failWithMessage("Message has not a valid DKIM signature");
      }
    } catch (IOException | FailException | MessagingException e) {
      failWithMessage("Message has not a valid DKIM signature: %s", e.getMessage());
    }
    return this;
  }

  /**
   * <p>Returns a {@code SignatureRecord} array assertion for the signature's records.</p>
   *
   * @return An assertion for the DKIM signature's records
   * @throws AssertionError If validating the signature fails
   */
  public ListAssert<SignatureRecord> records() {
    List<SignatureRecord> signatures = null;
    try {
      signatures = dkimVerifier.verify(partReader.toInputStream(actual));
    } catch (IOException | FailException | MessagingException e) {
      failWithMessage("Message has not a valid DKIM signature: %s", e.getMessage());
    }
    return Assertions.assertThat(signatures);
  }

  private static class RecordRetriever implements PublicKeyRecordRetriever {

    private final Map<String, String> publicKeys;

    public RecordRetriever(Map<String, String> publicKeys) {
      this.publicKeys = publicKeys;
    }

    @Override
    public List<String> getRecords(CharSequence methodAndOption, CharSequence selector,
                                   CharSequence token) {
      assertMethodAndOptionAreSupported(methodAndOption);
      String lookupName = buildLookupName(selector, token);
      String record = publicKeys.get(lookupName);
      if (Objects.isNull(record)) {
        return emptyList();
      } else {
        return singletonList(record);
      }
    }

    private void assertMethodAndOptionAreSupported(CharSequence methodAndOption) {
      if (Objects.equals(methodAndOption, "dns/txt")) {
        return;
      }
      throw new RuntimeException(format("Unsupported method and option: %s", methodAndOption));
    }

    private String buildLookupName(CharSequence selector, CharSequence token) {
      return format("%s._domainkey.%s", selector, token);
    }
  }
}
