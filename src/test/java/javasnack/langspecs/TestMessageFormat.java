package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

public class TestMessageFormat {

    @Test
    public void basicDemo() {
        final Object[] emptyArgs = new Object[] {};
        assertThat(new MessageFormat("'{0}'").format(emptyArgs)).isEqualTo("{0}");
        assertThat(new MessageFormat("''aa''").format(emptyArgs)).isEqualTo("'aa'");
        assertThat(new MessageFormat("'bb'").format(emptyArgs)).isEqualTo("bb");

        assertThat(new MessageFormat("{2} {0} {1}").format(new Object[] { 1234, 1234.56, "abc" }))
                .isEqualTo("abc 1,234 1,234.56");
        assertThat(new MessageFormat("{0} {0} {0}").format(new Object[] { 1234, 1234.56, "abc" }))
                .isEqualTo("1,234 1,234 1,234");

        assertThat(new MessageFormat("{0,number,integer}").format(new Object[] { 1234 })).isEqualTo("1,234");
        assertThat(new MessageFormat("{0,number,currency}", Locale.US).format(new Object[] { 1234 }))
                .isEqualTo("$1,234.00");
        assertThat(new MessageFormat("{0,number,currency}", Locale.JAPAN).format(new Object[] { 1234 }))
                .isEqualTo("￥1,234");
        assertThat(new MessageFormat("{0,number,percent}").format(new Object[] { 1234 })).isEqualTo("123,400%");
        assertThat(new MessageFormat("{0,number,#,###.#}").format(new Object[] { 1234 })).isEqualTo("1,234");

        assertThat(new MessageFormat("{0,number}").format(new Object[] { 1234.56 })).isEqualTo("1,234.56");
        assertThat(new MessageFormat("{0,number,#,###.###}").format(new Object[] { 1234.56 })).isEqualTo("1,234.56");
        assertThat(new MessageFormat("{0,number,0,000.000}").format(new Object[] { 1234.56 })).isEqualTo("1,234.560");

        final LocalDateTime ldt1 = LocalDateTime.of(2010, 1, 2, 3, 4, 5);
        final ZoneOffset zo1 = ZoneOffset.of("+09:00");
        final long epochSec = ldt1.toEpochSecond(zo1);
        final Date date1 = new Date(epochSec * 1000);
        final Object[] dates = new Object[] { date1 };

        final Locale l0 = Locale.US;
        final TimeZone tz0 = TimeZone.getDefault();
        final String tzs = tz0.getDisplayName(false, TimeZone.SHORT, Locale.US);
        final String tzl = tz0.getDisplayName(false, TimeZone.LONG, Locale.US);
        assertThat(new MessageFormat("{0,date}", l0).format(dates)).isEqualTo("Jan 2, 2010");
        assertThat(new MessageFormat("{0,date,short}", l0).format(dates)).isEqualTo("1/2/10");
        assertThat(new MessageFormat("{0,date,medium}", l0).format(dates)).isEqualTo("Jan 2, 2010");
        assertThat(new MessageFormat("{0,date,long}", l0).format(dates)).isEqualTo("January 2, 2010");
        assertThat(new MessageFormat("{0,date,full}", l0).format(dates)).isEqualTo("Saturday, January 2, 2010");
        assertThat(new MessageFormat("{0,time}", l0).format(dates)).isEqualTo("3:04:05 AM");
        assertThat(new MessageFormat("{0,time,short}", l0).format(dates)).isEqualTo("3:04 AM");
        assertThat(new MessageFormat("{0,time,medium}", l0).format(dates)).isEqualTo("3:04:05 AM");
        assertThat(new MessageFormat("{0,time,long}", l0).format(dates)).isEqualTo("3:04:05 AM " + tzs);
        assertThat(new MessageFormat("{0,time,full}", l0).format(dates)).isEqualTo("3:04:05 AM " + tzl);

        assertThat(new MessageFormat("{0,date,YYYY-MM-dd}", l0).format(dates)).isEqualTo("2010-01-02");
        assertThat(new MessageFormat("{0,time,YYYY-MM-dd}", l0).format(dates)).isEqualTo("2010-01-02");
        assertThat(new MessageFormat("{0,date,HH:mm:ss}", l0).format(dates)).isEqualTo("03:04:05");
        assertThat(new MessageFormat("{0,time,HH:mm:ss}", l0).format(dates)).isEqualTo("03:04:05");
        assertThat(new MessageFormat("{0,date,YYYY-MM-dd HH:mm:ss}", l0).format(dates))
                .isEqualTo("2010-01-02 03:04:05");
        assertThat(new MessageFormat("{0,time,YYYY-MM-dd HH:mm:ss}", l0).format(dates))
                .isEqualTo("2010-01-02 03:04:05");
    }

    @Test
    public void choiceFormatDemo() {
        // from java.text.MessageFormat javadoc example
        final String fmt1 = "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.";
        final Locale l0 = Locale.US;
        final MessageFormat mf1 = new MessageFormat(fmt1, l0);
        assertThat(mf1.format(new Object[] { 0 })).isEqualTo("There are no files.");
        assertThat(mf1.format(new Object[] { 1 })).isEqualTo("There is one file.");
        assertThat(mf1.format(new Object[] { 2 })).isEqualTo("There are 2 files.");
    }
}
