package javasnack.ojcp.se8silver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class Test12ApiDemoDateTime {

    @Test
    public void testLocalDateTimeDemo() {
        LocalDate date1 = LocalDate.of(2020, 2, 29);
        LocalTime time1 = LocalTime.of(23, 59, 59);
        LocalDateTime dt1 = LocalDateTime.of(2020, 2, 29, 23, 59, 59);

        LocalDate date2 = LocalDate.parse("2020-02-29");
        LocalTime time2 = LocalTime.parse("23:59:59");
        LocalDateTime dt2 = LocalDateTime.parse("2020-02-29T23:59:59");

        assertThat(date1).isEqualTo(date2);
        assertThat(time1).isEqualTo(time2);
        assertThat(dt1).isEqualTo(dt2);

        assertThatThrownBy(() -> {
            LocalDate.of(2019, 2, 29);
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalTime.of(24, 0, 0);
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalDateTime.of(2019, 2, 29, 23, 59, 59);
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalDate.parse("2020-2-29");
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalTime.parse("23:1:1");
        }).isInstanceOf(DateTimeException.class);

        assertThatThrownBy(() -> {
            LocalDateTime.parse("2020-02-29 23:59:59");
        }).isInstanceOf(DateTimeException.class);

        assertThat(date1.format(DateTimeFormatter.ISO_DATE)).isEqualTo("2020-02-29");
        assertThat(time1.format(DateTimeFormatter.ISO_TIME)).isEqualTo("23:59:59");
        assertThat(dt1.format(DateTimeFormatter.ISO_DATE_TIME)).isEqualTo("2020-02-29T23:59:59");
        assertThat(dt1.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))).isEqualTo("2020/02/29 23:59:59");
        date2 = LocalDate.parse("2020/02/29", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        assertThat(date1).isEqualTo(date2);

        date2 = date1.plusDays(1L);
        // >#>POINT<#<: 日付/時刻操作メソッドは元のオブジェクトは変更せず、新しいオブジェクトを返す。
        assertThat(date1.format(DateTimeFormatter.ISO_DATE)).isEqualTo("2020-02-29");
        assertThat(date2.format(DateTimeFormatter.ISO_DATE)).isEqualTo("2020-03-01");
        time2 = time1.plusSeconds(1L);
        assertThat(time1.format(DateTimeFormatter.ISO_TIME)).isEqualTo("23:59:59");
        assertThat(time2.format(DateTimeFormatter.ISO_TIME)).isEqualTo("00:00:00");

        date2 = date2.minusDays(1L);
        assertThat(date1).isEqualTo(date2);
        time2 = time2.minusSeconds(1L);
        assertThat(time1).isEqualTo(time2);
    }
}
