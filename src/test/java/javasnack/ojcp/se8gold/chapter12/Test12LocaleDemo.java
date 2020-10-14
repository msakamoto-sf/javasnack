package javasnack.ojcp.se8gold.chapter12;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class Test12LocaleDemo {
    @Test
    public void testSystemDefaultLocale() {
        System.out.println("--- see console log ---");
        final Locale lc0 = Locale.getDefault();
        System.out.println("system default locale:language=" + lc0.getLanguage());
        System.out.println("system default locale:script=" + lc0.getScript());
        System.out.println("system default locale:country=" + lc0.getCountry());
        System.out.println("system default locale:variant=" + lc0.getVariant());
        System.out.println("system default locale:language(display)=" + lc0.getDisplayLanguage());
        System.out.println("system default locale:script(display)=" + lc0.getDisplayScript());
        System.out.println("system default locale:country(display)=" + lc0.getDisplayCountry());
        System.out.println("system default locale:variant(display)=" + lc0.getDisplayVariant());
    }

    @Test
    public void testPredefinedLocaleDemo() {
        final Locale japan = Locale.JAPAN;
        final Locale japanese = Locale.JAPANESE;
        final Locale uk = Locale.UK;
        final Locale us = Locale.US;
        final Locale english = Locale.ENGLISH;
        final Locale canada = Locale.CANADA;
        final Locale canadaFr = Locale.CANADA_FRENCH;
        final Locale china = Locale.CHINA;
        final Locale chinese = Locale.CHINESE;
        final Locale chineseSimplified = Locale.SIMPLIFIED_CHINESE;
        final Locale chineseTraditional = Locale.TRADITIONAL_CHINESE;

        assertThat(japan.getLanguage()).isEqualTo("ja");
        assertThat(japan.getCountry()).isEqualTo("JP");
        assertThat(japan.getDisplayLanguage(japanese)).isEqualTo("日本語");
        assertThat(japan.getDisplayCountry(japanese)).isEqualTo("日本");
        assertThat(japan.getDisplayLanguage(english)).isEqualTo("Japanese");
        assertThat(japan.getDisplayCountry(english)).isEqualTo("Japan");
        assertThat(japan.getDisplayLanguage(chinese)).isEqualTo("日语");
        assertThat(japan.getDisplayCountry(chinese)).isEqualTo("日本");
        assertThat(japan.getDisplayLanguage(chineseSimplified)).isEqualTo("日语");
        assertThat(japan.getDisplayCountry(chineseSimplified)).isEqualTo("日本");
        assertThat(japan.getDisplayLanguage(chineseTraditional)).isEqualTo("日文");
        assertThat(japan.getDisplayCountry(chineseTraditional)).isEqualTo("日本");

        assertThat(uk.getLanguage()).isEqualTo("en");
        assertThat(uk.getCountry()).isEqualTo("GB");
        assertThat(uk.getDisplayLanguage(japanese)).isEqualTo("英語");
        assertThat(uk.getDisplayCountry(japanese)).isEqualTo("イギリス");
        assertThat(uk.getDisplayLanguage(english)).isEqualTo("English");
        assertThat(uk.getDisplayCountry(english)).isEqualTo("United Kingdom");
        assertThat(uk.getDisplayLanguage(chinese)).isEqualTo("英语");
        assertThat(uk.getDisplayCountry(chinese)).isEqualTo("英国");
        assertThat(uk.getDisplayLanguage(chineseSimplified)).isEqualTo("英语");
        assertThat(uk.getDisplayCountry(chineseSimplified)).isEqualTo("英国");
        assertThat(uk.getDisplayLanguage(chineseTraditional)).isEqualTo("英文");
        assertThat(uk.getDisplayCountry(chineseTraditional)).isEqualTo("英國");

        assertThat(us.getLanguage()).isEqualTo("en");
        assertThat(us.getCountry()).isEqualTo("US");
        assertThat(us.getDisplayLanguage(japanese)).isEqualTo("英語");
        assertThat(us.getDisplayCountry(japanese)).isEqualTo("アメリカ合衆国");
        assertThat(us.getDisplayLanguage(english)).isEqualTo("English");
        assertThat(us.getDisplayCountry(english)).isEqualTo("United States");
        assertThat(us.getDisplayLanguage(chinese)).isEqualTo("英语");
        assertThat(us.getDisplayCountry(chinese)).isEqualTo("美国");
        assertThat(us.getDisplayLanguage(chineseSimplified)).isEqualTo("英语");
        assertThat(us.getDisplayCountry(chineseSimplified)).isEqualTo("美国");
        assertThat(us.getDisplayLanguage(chineseTraditional)).isEqualTo("英文");
        assertThat(us.getDisplayCountry(chineseTraditional)).isEqualTo("美國");

        assertThat(canada.getLanguage()).isEqualTo("en");
        assertThat(canada.getCountry()).isEqualTo("CA");
        assertThat(canada.getDisplayLanguage(japanese)).isEqualTo("英語");
        assertThat(canada.getDisplayCountry(japanese)).isEqualTo("カナダ");
        assertThat(canada.getDisplayLanguage(english)).isEqualTo("English");
        assertThat(canada.getDisplayCountry(english)).isEqualTo("Canada");
        assertThat(canada.getDisplayLanguage(chinese)).isEqualTo("英语");
        assertThat(canada.getDisplayCountry(chinese)).isEqualTo("加拿大");
        assertThat(canada.getDisplayLanguage(chineseSimplified)).isEqualTo("英语");
        assertThat(canada.getDisplayCountry(chineseSimplified)).isEqualTo("加拿大");
        assertThat(canada.getDisplayLanguage(chineseTraditional)).isEqualTo("英文");
        assertThat(canada.getDisplayCountry(chineseTraditional)).isEqualTo("加拿大");

        assertThat(canadaFr.getLanguage()).isEqualTo("fr");
        assertThat(canadaFr.getCountry()).isEqualTo("CA");
        assertThat(canadaFr.getDisplayLanguage(japanese)).isEqualTo("フランス語");
        assertThat(canadaFr.getDisplayCountry(japanese)).isEqualTo("カナダ");
        assertThat(canadaFr.getDisplayLanguage(english)).isEqualTo("French");
        assertThat(canadaFr.getDisplayCountry(english)).isEqualTo("Canada");
        assertThat(canadaFr.getDisplayLanguage(chinese)).isEqualTo("法语");
        assertThat(canadaFr.getDisplayCountry(chinese)).isEqualTo("加拿大");
        assertThat(canadaFr.getDisplayLanguage(chineseSimplified)).isEqualTo("法语");
        assertThat(canadaFr.getDisplayCountry(chineseSimplified)).isEqualTo("加拿大");
        assertThat(canadaFr.getDisplayLanguage(chineseTraditional)).isEqualTo("法文");
        assertThat(canadaFr.getDisplayCountry(chineseTraditional)).isEqualTo("加拿大");

        assertThat(china.getLanguage()).isEqualTo("zh");
        assertThat(china.getCountry()).isEqualTo("CN");
        assertThat(china.getDisplayLanguage(japanese)).isEqualTo("中国語");
        assertThat(china.getDisplayCountry(japanese)).isEqualTo("中国");
        assertThat(china.getDisplayLanguage(english)).isEqualTo("Chinese");
        assertThat(china.getDisplayCountry(english)).isEqualTo("China");
        assertThat(china.getDisplayLanguage(chinese)).isEqualTo("中文");
        assertThat(china.getDisplayCountry(chinese)).isEqualTo("中国");
        assertThat(china.getDisplayLanguage(chineseSimplified)).isEqualTo("中文");
        assertThat(china.getDisplayCountry(chineseSimplified)).isEqualTo("中国");
        assertThat(china.getDisplayLanguage(chineseTraditional)).isEqualTo("中文");
        assertThat(china.getDisplayCountry(chineseTraditional)).isEqualTo("中國");

        // 言語用の locale ではcountry(region)が空文字列
        assertThat(japanese.getLanguage()).isEqualTo("ja");
        assertThat(japanese.getCountry()).isEqualTo("");
        assertThat(japanese.getDisplayLanguage(japanese)).isEqualTo("日本語");
        assertThat(japanese.getDisplayCountry(japanese)).isEqualTo("");
        assertThat(japanese.getDisplayLanguage(english)).isEqualTo("Japanese");
        assertThat(japanese.getDisplayCountry(english)).isEqualTo("");
        assertThat(japanese.getDisplayLanguage(chinese)).isEqualTo("日语");
        assertThat(japanese.getDisplayCountry(chinese)).isEqualTo("");
        assertThat(japanese.getDisplayLanguage(chineseSimplified)).isEqualTo("日语");
        assertThat(japanese.getDisplayCountry(chineseSimplified)).isEqualTo("");
        assertThat(japanese.getDisplayLanguage(chineseTraditional)).isEqualTo("日文");
        assertThat(japanese.getDisplayCountry(chineseTraditional)).isEqualTo("");

        assertThat(english.getLanguage()).isEqualTo("en");
        assertThat(english.getCountry()).isEqualTo("");
        assertThat(english.getDisplayLanguage(japanese)).isEqualTo("英語");
        assertThat(english.getDisplayCountry(japanese)).isEqualTo("");
        assertThat(english.getDisplayLanguage(english)).isEqualTo("English");
        assertThat(english.getDisplayCountry(english)).isEqualTo("");
        assertThat(english.getDisplayLanguage(chinese)).isEqualTo("英语");
        assertThat(english.getDisplayCountry(chinese)).isEqualTo("");
        assertThat(english.getDisplayLanguage(chineseSimplified)).isEqualTo("英语");
        assertThat(english.getDisplayCountry(chineseSimplified)).isEqualTo("");
        assertThat(english.getDisplayLanguage(chineseTraditional)).isEqualTo("英文");
        assertThat(english.getDisplayCountry(chineseTraditional)).isEqualTo("");
    }

    @Test
    public void testLocaleCreationDemo() {
        Locale loc0 = new Locale("aa");
        assertThat(loc0.getLanguage()).isEqualTo("aa");
        assertThat(loc0.getCountry()).isEqualTo("");
        assertThat(loc0.getVariant()).isEqualTo("");
        assertThat(loc0.getScript()).isEqualTo("");
        loc0 = new Locale("bb", "CC");
        assertThat(loc0.getLanguage()).isEqualTo("bb");
        assertThat(loc0.getCountry()).isEqualTo("CC");
        assertThat(loc0.getVariant()).isEqualTo("");
        assertThat(loc0.getScript()).isEqualTo("");
        loc0 = new Locale("dd", "EE", "ff");
        assertThat(loc0.getLanguage()).isEqualTo("dd");
        assertThat(loc0.getCountry()).isEqualTo("EE");
        assertThat(loc0.getVariant()).isEqualTo("ff");
        assertThat(loc0.getScript()).isEqualTo("");

        loc0 = new Locale.Builder().setLanguage("aa").build();
        assertThat(loc0.getLanguage()).isEqualTo("aa");
        assertThat(loc0.getCountry()).isEqualTo("");
        assertThat(loc0.getVariant()).isEqualTo("");
        assertThat(loc0.getScript()).isEqualTo("");
        loc0 = new Locale.Builder().setLanguage("bb").setRegion("CC").build();
        assertThat(loc0.getLanguage()).isEqualTo("bb");
        assertThat(loc0.getCountry()).isEqualTo("CC");
        assertThat(loc0.getVariant()).isEqualTo("");
        assertThat(loc0.getScript()).isEqualTo("");
        loc0 = new Locale.Builder().setLanguage("dd").setRegion("EE").setVariant("POSIX").build();
        assertThat(loc0.getLanguage()).isEqualTo("dd");
        assertThat(loc0.getCountry()).isEqualTo("EE");
        assertThat(loc0.getVariant()).isEqualTo("POSIX");
        assertThat(loc0.getScript()).isEqualTo("");
        loc0 = new Locale.Builder().setLanguage("gg").setRegion("HH").setVariant("POSIX").setScript("XXXX").build();
        assertThat(loc0.getLanguage()).isEqualTo("gg");
        assertThat(loc0.getCountry()).isEqualTo("HH");
        assertThat(loc0.getVariant()).isEqualTo("POSIX");
        assertThat(loc0.getScript()).isEqualTo("Xxxx");
    }
}
