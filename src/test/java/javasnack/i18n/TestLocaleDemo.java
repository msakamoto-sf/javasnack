/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javasnack.i18n;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class TestLocaleDemo {

    @Test
    public void localeDemo() {
        final Locale l1 = Locale.US;
        assertThat(l1.getCountry()).isEqualTo("US");
        assertThat(l1.getLanguage()).isEqualTo("en");
        assertThat(l1.getDisplayCountry(Locale.ENGLISH)).isEqualTo("United States");
        assertThat(l1.getDisplayCountry(Locale.JAPANESE)).isEqualTo("アメリカ合衆国");
        assertThat(l1.getDisplayLanguage(Locale.ENGLISH)).isEqualTo("English");
        assertThat(l1.getDisplayLanguage(Locale.JAPANESE)).isEqualTo("英語");
        assertThat(l1.getDisplayName(Locale.ENGLISH)).isEqualTo("English (United States)");
        assertThat(l1.getDisplayName(Locale.JAPANESE)).isEqualTo("英語 (アメリカ合衆国)");
        assertThat(l1.getScript()).isEqualTo("");
        assertThat(l1.getISO3Country()).isEqualTo("USA");
        assertThat(l1.getISO3Language()).isEqualTo("eng");
        assertThat(l1.toString()).isEqualTo("en_US");

        final Locale l2 = Locale.ENGLISH;
        assertThat(l2.getCountry()).isEqualTo("");
        assertThat(l2.getLanguage()).isEqualTo("en");
        assertThat(l2.getDisplayCountry(Locale.ENGLISH)).isEqualTo("");
        assertThat(l2.getDisplayCountry(Locale.JAPANESE)).isEqualTo("");
        assertThat(l2.getDisplayLanguage(Locale.ENGLISH)).isEqualTo("English");
        assertThat(l2.getDisplayLanguage(Locale.JAPANESE)).isEqualTo("英語");
        assertThat(l2.getDisplayName(Locale.ENGLISH)).isEqualTo("English");
        assertThat(l2.getDisplayName(Locale.JAPANESE)).isEqualTo("英語");
        assertThat(l2.getScript()).isEqualTo("");
        assertThat(l2.getISO3Country()).isEqualTo("");
        assertThat(l2.getISO3Language()).isEqualTo("eng");
        assertThat(l2.toString()).isEqualTo("en");

        final Locale l3 = Locale.JAPAN;
        assertThat(l3.getCountry()).isEqualTo("JP");
        assertThat(l3.getLanguage()).isEqualTo("ja");
        assertThat(l3.getDisplayCountry(Locale.ENGLISH)).isEqualTo("Japan");
        assertThat(l3.getDisplayCountry(Locale.JAPANESE)).isEqualTo("日本");
        assertThat(l3.getDisplayLanguage(Locale.ENGLISH)).isEqualTo("Japanese");
        assertThat(l3.getDisplayLanguage(Locale.JAPANESE)).isEqualTo("日本語");
        assertThat(l3.getDisplayName(Locale.ENGLISH)).isEqualTo("Japanese (Japan)");
        assertThat(l3.getDisplayName(Locale.JAPANESE)).isEqualTo("日本語 (日本)");
        assertThat(l3.getScript()).isEqualTo("");
        assertThat(l3.getISO3Country()).isEqualTo("JPN");
        assertThat(l3.getISO3Language()).isEqualTo("jpn");
        assertThat(l3.toString()).isEqualTo("ja_JP");

        final Locale l4 = Locale.JAPANESE;
        assertThat(l4.getCountry()).isEqualTo("");
        assertThat(l4.getLanguage()).isEqualTo("ja");
        assertThat(l4.getDisplayCountry(Locale.ENGLISH)).isEqualTo("");
        assertThat(l4.getDisplayCountry(Locale.JAPANESE)).isEqualTo("");
        assertThat(l4.getDisplayLanguage(Locale.ENGLISH)).isEqualTo("Japanese");
        assertThat(l4.getDisplayLanguage(Locale.JAPANESE)).isEqualTo("日本語");
        assertThat(l4.getDisplayName(Locale.ENGLISH)).isEqualTo("Japanese");
        assertThat(l4.getDisplayName(Locale.JAPANESE)).isEqualTo("日本語");
        assertThat(l4.getScript()).isEqualTo("");
        assertThat(l4.getISO3Country()).isEqualTo("");
        assertThat(l4.getISO3Language()).isEqualTo("jpn");
        assertThat(l4.toString()).isEqualTo("ja");
    }
}
