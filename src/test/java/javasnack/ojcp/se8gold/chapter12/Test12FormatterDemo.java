package javasnack.ojcp.se8gold.chapter12;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Locale;

import org.junit.jupiter.api.Test;

public class Test12FormatterDemo {
    @Test
    public void testNumberFormatDemo() throws ParseException {
        NumberFormat nf0 = NumberFormat.getInstance();
        NumberFormat nfc0 = NumberFormat.getCurrencyInstance();
        NumberFormat nfi0 = NumberFormat.getIntegerInstance();
        System.out.println("--- see console log ---");
        System.out.println("system default number format : " + nf0.format(-10_000_000.123));
        System.out.println("system default currency format : " + nfc0.format(10_000_000.123));
        System.out.println("system default integer format : " + nfi0.format(-10_000_000.123));

        nf0 = NumberFormat.getInstance(Locale.JAPAN);
        nfc0 = NumberFormat.getCurrencyInstance(Locale.JAPAN);
        nfi0 = NumberFormat.getIntegerInstance(Locale.JAPAN);
        assertThat(nf0.format(-10_000_000.123)).isEqualTo("-10,000,000.123");
        assertThat(nfc0.format(10_000_000.123)).isEqualTo("￥10,000,000"); // 全角円記号
        assertThat(nfi0.format(-10_000_000.123)).isEqualTo("-10,000,000");

        nf0 = NumberFormat.getInstance(Locale.US);
        nfc0 = NumberFormat.getCurrencyInstance(Locale.US);
        nfi0 = NumberFormat.getIntegerInstance(Locale.US);
        assertThat(nf0.format(-10_000_000.123)).isEqualTo("-10,000,000.123");
        assertThat(nfc0.format(10_000_000.123)).isEqualTo("$10,000,000.12");
        assertThat(nfi0.format(-10_000_000.123)).isEqualTo("-10,000,000");

        nf0 = NumberFormat.getInstance(Locale.FRANCE);
        nfc0 = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        nfi0 = NumberFormat.getIntegerInstance(Locale.FRANCE);
        assertThat(nf0.format(-10_000_000.123)).isEqualTo("-10 000 000,123");
        assertThat(nfc0.format(10_000_000.123)).isEqualTo("10 000 000,12 €");
        assertThat(nfi0.format(-10_000_000.123)).isEqualTo("-10 000 000");

        nf0 = NumberFormat.getInstance(Locale.US);
        Number n0 = nf0.parse("-10,000.123");
        assertThat(n0).isEqualTo(-10_000.123);
        n0 = nf0.parse("-10_000:123");
        assertThat(n0).isEqualTo(-10L); // parse できるところで止まる

        assertThatThrownBy(() -> {
            NumberFormat.getInstance(Locale.US).parse("abc");
        }).isInstanceOf(ParseException.class);

        nfc0 = NumberFormat.getCurrencyInstance(Locale.US);
        n0 = nfc0.parse("$10,000.12");
        assertThat(n0).isEqualTo(10_000.12);

        nfc0 = NumberFormat.getCurrencyInstance(Locale.JAPAN);
        n0 = nfc0.parse("￥10,000");
        assertThat(n0).isEqualTo(10_000L);

        assertThatThrownBy(() -> {
            NumberFormat.getCurrencyInstance(Locale.US).parse("abc");
        }).isInstanceOf(ParseException.class);

        assertThatThrownBy(() -> {
            // 半角 0x5C は受け付けない
            NumberFormat.getCurrencyInstance(Locale.JAPAN).parse("\\123");
        }).isInstanceOf(ParseException.class);
    }

    static String decimalFormat(final String pattern, final double v) {
        final DecimalFormat df = new DecimalFormat(pattern);
        return df.format(v);
    }

    @Test
    public void testDecimalFormatDemo() {
        assertThat(decimalFormat("###,###,###", 9_123_456_789.123)).isEqualTo("9,123,456,789");
        assertThat(decimalFormat("###,###,###", -9_123_456_789.123)).isEqualTo("-9,123,456,789");
        assertThat(decimalFormat("###.##", 9_123_456_789.123)).isEqualTo("9123456789.12");
        assertThat(decimalFormat("###.##", -9_123_456_789.123)).isEqualTo("-9123456789.12");
        assertThat(decimalFormat("000000.000", 123.4567)).isEqualTo("000123.457");
        assertThat(decimalFormat("000000.00", 123.4567)).isEqualTo("000123.46");
        assertThat(decimalFormat("000000.0", 123.4567)).isEqualTo("000123.5");
        assertThat(decimalFormat("000000.000", -123.4567)).isEqualTo("-000123.457");
        assertThat(decimalFormat("000000.00", -123.4567)).isEqualTo("-000123.46");
        assertThat(decimalFormat("000000.0", -123.4567)).isEqualTo("-000123.5");
        assertThat(decimalFormat("$###,###.###", 9_123_456_789.123)).isEqualTo("$9,123,456,789.123");
        assertThat(decimalFormat("$###,###.###", -9_123_456_789.123)).isEqualTo("-$9,123,456,789.123");

        System.out.println("--- see console log ---");
        System.out.println(decimalFormat("\u00a4###,###.###", 9_123_456_789.123));
        System.out.println(decimalFormat("\u00a5###,###.###", 9_123_456_789.123));
        /* NOTE : 教科書では \u00a5 が通貨記号になるとあるが、
         * javadoc によれば \u00a4 が通貨記号になる。
         * ref: https://docs.oracle.com/javase/jp/8/docs/api/java/text/DecimalFormat.html
         * Locale.JAPAN 環境では \u00a4 は全角円記号になる。
         * \u00a5 はもともと Unicode の YEN SIGN になり、
         * \u00a4 は Unicode では国際通貨記号, CURRENCY SIGN なので、
         * javadoc の \u00a4 が正しい気がする。
         */
    }

    @Test
    public void testDateTimeFormatDemo() {
        LocalDate ld0 = LocalDate.of(2019, Month.DECEMBER, 31);
        assertThat(ld0.getYear()).isEqualTo(2019);
        assertThat(ld0.getMonth()).isEqualTo(Month.DECEMBER);
        assertThat(ld0.getMonthValue()).isEqualTo(12);
        assertThat(ld0.getDayOfYear()).isEqualTo(365);
        assertThat(ld0.getDayOfMonth()).isEqualTo(31);
        assertThat(ld0.getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);

        ld0 = LocalDate.of(2020, Month.JANUARY, 1);
        assertThat(ld0.getYear()).isEqualTo(2020);
        assertThat(ld0.getMonth()).isEqualTo(Month.JANUARY);
        assertThat(ld0.getMonthValue()).isEqualTo(1);
        assertThat(ld0.getDayOfYear()).isEqualTo(1);
        assertThat(ld0.getDayOfMonth()).isEqualTo(1);
        assertThat(ld0.getDayOfWeek()).isEqualTo(DayOfWeek.WEDNESDAY);

        ld0 = LocalDate.of(2020, Month.DECEMBER, 31);
        assertThat(ld0.getYear()).isEqualTo(2020);
        assertThat(ld0.getMonth()).isEqualTo(Month.DECEMBER);
        assertThat(ld0.getMonthValue()).isEqualTo(12);
        assertThat(ld0.getDayOfYear()).isEqualTo(366); // leap year
        assertThat(ld0.getDayOfMonth()).isEqualTo(31);
        assertThat(ld0.getDayOfWeek()).isEqualTo(DayOfWeek.THURSDAY);

        final LocalDate ld1 = LocalDate.of(2020, Month.DECEMBER, 31);
        final LocalTime lt1 = LocalTime.of(12, 34, 56);
        final LocalDateTime ldt1 = LocalDateTime.of(ld1, lt1);

        // short : US locale demo
        assertThat(dtfmtShortDate(ld1, Locale.US)).isEqualTo("12/31/20");
        assertThatThrownBy(() -> {
            dtfmtShortDate(lt1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThat(dtfmtShortDate(ldt1, Locale.US)).isEqualTo("12/31/20");

        assertThatThrownBy(() -> {
            dtfmtShortTime(ld1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThat(dtfmtShortTime(lt1, Locale.US)).isEqualTo("12:34 PM");
        assertThat(dtfmtShortTime(ldt1, Locale.US)).isEqualTo("12:34 PM");

        assertThatThrownBy(() -> {
            dtfmtShortDateTime(ld1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThatThrownBy(() -> {
            dtfmtShortDateTime(lt1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThat(dtfmtShortDateTime(ldt1, Locale.US)).isEqualTo("12/31/20, 12:34 PM");

        // medium : US locale demo
        assertThat(dtfmtMediumDate(ld1, Locale.US)).isEqualTo("Dec 31, 2020");
        assertThatThrownBy(() -> {
            dtfmtMediumDate(lt1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThat(dtfmtMediumDate(ldt1, Locale.US)).isEqualTo("Dec 31, 2020");

        assertThatThrownBy(() -> {
            dtfmtMediumTime(ld1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThat(dtfmtMediumTime(lt1, Locale.US)).isEqualTo("12:34:56 PM");
        assertThat(dtfmtMediumTime(ldt1, Locale.US)).isEqualTo("12:34:56 PM");

        assertThatThrownBy(() -> {
            dtfmtMediumDateTime(ld1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThatThrownBy(() -> {
            dtfmtMediumDateTime(lt1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThat(dtfmtMediumDateTime(ldt1, Locale.US)).isEqualTo("Dec 31, 2020, 12:34:56 PM");

        // long : US locale demo
        assertThat(dtfmtLongDate(ld1, Locale.US)).isEqualTo("December 31, 2020");
        assertThatThrownBy(() -> {
            dtfmtLongDate(lt1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThat(dtfmtLongDate(ldt1, Locale.US)).isEqualTo("December 31, 2020");

        assertThatThrownBy(() -> {
            dtfmtLongTime(ld1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThatThrownBy(() -> {
            dtfmtLongTime(lt1, Locale.US);
        }).isInstanceOf(DateTimeException.class); // zoneId情報欠落による例外
        assertThatThrownBy(() -> {
            dtfmtLongTime(ldt1, Locale.US);
        }).isInstanceOf(DateTimeException.class); // zoneId情報欠落による例外

        assertThatThrownBy(() -> {
            dtfmtLongDateTime(ld1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThatThrownBy(() -> {
            dtfmtLongDateTime(lt1, Locale.US);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThatThrownBy(() -> {
            dtfmtLongDateTime(ldt1, Locale.US);
        }).isInstanceOf(DateTimeException.class); // zoneId情報欠落による例外

        System.out.println("--- see console log ---");
        // short : default locale demo
        System.out.println(dtfmtShortDate(ld1));
        System.out.println(dtfmtShortDate(ldt1));

        System.out.println(dtfmtShortTime(lt1));
        System.out.println(dtfmtShortTime(ldt1));

        System.out.println(dtfmtShortDateTime(ldt1));

        // medium : default locale demo
        System.out.println(dtfmtMediumDate(ld1));
        System.out.println(dtfmtMediumDate(ldt1));

        System.out.println(dtfmtMediumTime(lt1));
        System.out.println(dtfmtMediumTime(ldt1));

        System.out.println(dtfmtMediumDateTime(ldt1));

        // long : default locale demo
        System.out.println(dtfmtLongDate(ld1));
        System.out.println(dtfmtLongDate(ldt1));
    }

    static String dtfmtShortDate(final TemporalAccessor temporal) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(temporal);
    }

    static String dtfmtShortDate(final TemporalAccessor temporal, final Locale locale) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale).format(temporal);
    }

    static String dtfmtShortTime(final TemporalAccessor temporal) {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(temporal);
    }

    static String dtfmtShortTime(final TemporalAccessor temporal, final Locale locale) {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale).format(temporal);
    }

    static String dtfmtShortDateTime(final TemporalAccessor temporal) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(temporal);
    }

    static String dtfmtShortDateTime(final TemporalAccessor temporal, final Locale locale) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale).format(temporal);
    }

    static String dtfmtMediumDate(final TemporalAccessor temporal) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(temporal);
    }

    static String dtfmtMediumDate(final TemporalAccessor temporal, final Locale locale) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale).format(temporal);
    }

    static String dtfmtMediumTime(final TemporalAccessor temporal) {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).format(temporal);
    }

    static String dtfmtMediumTime(final TemporalAccessor temporal, final Locale locale) {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(locale).format(temporal);
    }

    static String dtfmtMediumDateTime(final TemporalAccessor temporal) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(temporal);
    }

    static String dtfmtMediumDateTime(final TemporalAccessor temporal, final Locale locale) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale).format(temporal);
    }

    static String dtfmtLongDate(final TemporalAccessor temporal) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(temporal);
    }

    static String dtfmtLongDate(final TemporalAccessor temporal, final Locale locale) {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale).format(temporal);
    }

    static String dtfmtLongTime(final TemporalAccessor temporal) {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.LONG).format(temporal);
    }

    static String dtfmtLongTime(final TemporalAccessor temporal, final Locale locale) {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.LONG).withLocale(locale).format(temporal);
    }

    static String dtfmtLongDateTime(final TemporalAccessor temporal) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).format(temporal);
    }

    static String dtfmtLongDateTime(final TemporalAccessor temporal, final Locale locale) {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(locale).format(temporal);
    }

    @Test
    public void testCustomDateTimeFormatPatternDemo() {
        final LocalDate ld0 = LocalDate.of(2020, Month.DECEMBER, 31);
        final LocalTime lt0 = LocalTime.of(12, 34, 56);
        final LocalDateTime ldt0 = LocalDateTime.of(ld0, lt0);

        final DateTimeFormatter fmt0 = DateTimeFormatter.ofPattern("yyyy MM dd");
        assertThat(fmt0.format(ld0)).isEqualTo("2020 12 31");
        assertThatThrownBy(() -> {
            fmt0.format(lt0);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThat(fmt0.format(ldt0)).isEqualTo("2020 12 31");
        assertThat(LocalDate.parse("2020 12 31", fmt0)).isEqualTo(ld0);

        // ofPattern() では ' -> '' にエスケープする
        final DateTimeFormatter fmt1 = DateTimeFormatter.ofPattern("HH''mm''ss");
        assertThatThrownBy(() -> {
            fmt1.format(ld0);
        }).isInstanceOf(UnsupportedTemporalTypeException.class);
        assertThat(fmt1.format(lt0)).isEqualTo("12'34'56");
        assertThat(fmt1.format(ldt0)).isEqualTo("12'34'56");
        assertThat(LocalTime.parse("12'34'56", fmt1)).isEqualTo(lt0);

        // locale 依存の format デモ + リテラルは '' 囲みのデモ
        final DateTimeFormatter fmt2 = DateTimeFormatter.ofPattern("EEE, MMM d, ''yy 'xxx'");
        assertThat(fmt2.withLocale(Locale.US).format(ld0)).isEqualTo("Thu, Dec 31, '20 xxx");
        assertThat(fmt2.withLocale(Locale.JAPAN).format(ld0)).isEqualTo("木, 12月 31, '20 xxx");
    }
}
