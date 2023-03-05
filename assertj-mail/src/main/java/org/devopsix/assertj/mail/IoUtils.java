package org.devopsix.assertj.mail;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

final class IoUtils {

  private IoUtils() {
  }

  static byte[] toByteArray(final InputStream input) throws IOException {
    requireNonNull(input);
    try (final ByteArrayOutputStream output = new ByteArrayOutputStream()) {
      final byte[] buffer = new byte[4096];
      int n;
      while ((n = input.read(buffer)) != -1) {
        output.write(buffer, 0, n);
      }
      return output.toByteArray();
    }
  }
}
