package org.devopsix.assertj.mail;

import static java.time.chrono.IsoChronology.INSTANCE;
import static java.time.format.ResolverStyle.SMART;
import static java.time.format.SignStyle.NOT_NEGATIVE;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;
import static java.util.Objects.isNull;
import static java.util.regex.Pattern.compile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * <p>This class supplies a date time formatter relaxed enough to read both,
 * RFC 822 (two-digit year) and RFC 2822/1123 (four-digit year) notation.</p>
 *
 * @author devopsix
 */
final class MailDateTimeFormatter {

  private MailDateTimeFormatter() {
  }

  private static Map<Long, String> dayNames() {
    Map<Long, String> dayNames = new HashMap<>();
    dayNames.put(1L, "Mon");
    dayNames.put(2L, "Tue");
    dayNames.put(3L, "Wed");
    dayNames.put(4L, "Thu");
    dayNames.put(5L, "Fri");
    dayNames.put(6L, "Sat");
    dayNames.put(7L, "Sun");
    return dayNames;
  }

  private static Map<Long, String> monthNames() {
    Map<Long, String> monthNames = new HashMap<>();
    monthNames.put(1L, "Jan");
    monthNames.put(2L, "Feb");
    monthNames.put(3L, "Mar");
    monthNames.put(4L, "Apr");
    monthNames.put(5L, "May");
    monthNames.put(6L, "Jun");
    monthNames.put(7L, "Jul");
    monthNames.put(8L, "Aug");
    monthNames.put(9L, "Sep");
    monthNames.put(10L, "Oct");
    monthNames.put(11L, "Nov");
    monthNames.put(12L, "Dec");
    return monthNames;
  }

  // We don't want these names to depend on the user/app locale.
  // English locale always applies to date headers in mails.
  private static final Map<Long, String> DAY_NAMES = dayNames();
  private static final Map<Long, String> MONTH_NAMES = monthNames();

  static final DateTimeFormatter MAIL_DATE_TIME = new DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .parseLenient()
      .optionalStart()
      .appendText(DAY_OF_WEEK, DAY_NAMES)
      .appendLiteral(", ")
      .optionalEnd()
      .appendValue(DAY_OF_MONTH, 1, 2, NOT_NEGATIVE)
      .appendLiteral(' ')
      .appendText(MONTH_OF_YEAR, MONTH_NAMES)
      .appendLiteral(' ')
      .appendValueReduced(YEAR, 2, 4, LocalDate.of(1900, 1, 1))
      .appendLiteral(' ')
      .appendValue(HOUR_OF_DAY, 2)
      .appendLiteral(':')
      .appendValue(MINUTE_OF_HOUR, 2)
      .optionalStart()
      .appendLiteral(':')
      .appendValue(SECOND_OF_MINUTE, 2)
      .optionalEnd()
      .appendLiteral(' ')
      .appendOffset("+HHMM", "GMT")
      .toFormatter()
      .withResolverStyle(SMART)
      .withChronology(INSTANCE);

  private static final Pattern TRAILING_ZONE = compile("\\s+\\([^(]+\\)\\s*$");

  /**
   * <p>Trims trailing time zone name in parentheses after the zone offset.</p>
   *
   * <p>Some mailer programs seem to append the zone name although that is not covered by the
   * RFCs.</p>
   *
   * @param value RFC 822/2822/1123 date time string
   * @return date time string with trailing time zone name removed
   */
  static String trimTrailingZoneText(String value) {
    if (isNull(value)) {
      return null;
    }
    return TRAILING_ZONE.matcher(value).replaceAll("");
  }
}
