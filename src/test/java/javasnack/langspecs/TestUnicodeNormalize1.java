/*
 * Copyright 2015 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.text.Normalizer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import lombok.EqualsAndHashCode;

/* see:
 * http://hydrocul.github.io/wiki/blog/2014/1101-hyphen-minus-wave-tilde.html
 * http://hydrocul.github.io/wiki/blog/2014/1127-unicode-nfkd-mb-convert-kana.html
 * http://nomenclator.la.coocan.jp/unicode/normalization.htm
 * http://itpro.nikkeibp.co.jp/article/COLUMN/20071130/288467/
 * http://docs.oracle.com/javase/tutorial/i18n/text/normalizerapi.html
 * http://www.javainthebox.net/laboratory/JavaSE6/normalizer/normalizer.html
 * http://blog.mwsoft.jp/article/34823291.html
 * 
 * -> NFKC is useful to simple string normalization. (with some exception)
 */
public class TestUnicodeNormalize1 {

    @EqualsAndHashCode
    static class NormalizeResult {
        String uc2hex(String src) {
            StringBuilder sb = new StringBuilder();
            for (char c : src.toCharArray()) {
                sb.append(String.format("%x ", (int) c));
            }
            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("NormalizeResult [NFC=");
            sb.append(NFC);
            sb.append("(" + uc2hex(NFC) + "),");
            sb.append("NFD=" + NFD);
            sb.append("(" + uc2hex(NFD) + "),");
            sb.append("NFKC=" + NFKC);
            sb.append("(" + uc2hex(NFKC) + "),");
            sb.append("NFKD=" + NFKD);
            sb.append("(" + uc2hex(NFKD) + ")]");
            return sb.toString();
        }

        public NormalizeResult(String nFC, String nFD, String nFKC, String nFKD) {
            NFC = nFC;
            NFD = nFD;
            NFKC = nFKC;
            NFKD = nFKD;
        }

        public NormalizeResult() {
        }

        /** Canonical decomposition, followed by canonical composition. */
        String NFC;
        /** Canonical decomposition. */
        String NFD;
        /** Compatibility decomposition, followed by canonical composition. */
        String NFKC;
        /** Compatibility decomposition. */
        String NFKD;
    }

    NormalizeResult normalize(String s) {
        NormalizeResult nr = new NormalizeResult();
        nr.NFC = Normalizer.normalize(s, Normalizer.Form.NFC);
        nr.NFD = Normalizer.normalize(s, Normalizer.Form.NFD);
        nr.NFKC = Normalizer.normalize(s, Normalizer.Form.NFKC);
        nr.NFKD = Normalizer.normalize(s, Normalizer.Form.NFKD);
        return nr;
    }

    static Stream<Arguments> provideNomalizeSamples() {
        return Stream.of(
        // @formatter:off
        // japanese zenkaku hiragana "a"
        arguments( "\u3042", new NormalizeResult("\u3042", "\u3042", "\u3042", "\u3042") ),
        // japanese zenkaku hiragana "ga"
        arguments( "\u304c", new NormalizeResult("\u304c", "\u304B\u3099", "\u304c", "\u304b\u3099") ),
        // japanese zenkaku hiragana "pu"
        arguments( "\u3077", new NormalizeResult("\u3077", "\u3075\u309a", "\u3077", "\u3075\u309a") ),
        // japanese zenkaku hiragana small "tsu"
        arguments( "\u3063", new NormalizeResult("\u3063", "\u3063", "\u3063", "\u3063") ),
        // japanese zenkaku hiragana "wa" + japanese dakuten
        arguments( "\u308f\u3099", new NormalizeResult("\u308f\u3099", "\u308f\u3099", "\u308f\u3099", "\u308f\u3099") ),
        // japanese zenkaku hiragana "ka" + japanese han-dakuten
        arguments( "\u304b\u309a", new NormalizeResult("\u304b\u309a", "\u304b\u309a", "\u304b\u309a", "\u304b\u309a") ),
        // japanese zenkaku katakana "a"
        arguments( "\u30a2", new NormalizeResult("\u30a2", "\u30a2", "\u30a2", "\u30a2") ),
        // japanese zenkaku katakana "vu"
        arguments( "\u30f4", new NormalizeResult("\u30f4", "\u30a6\u3099", "\u30f4", "\u30a6\u3099") ),
        // japanese zenkaku katakana "po"
        arguments( "\u30dd", new NormalizeResult("\u30dd", "\u30db\u309a", "\u30dd", "\u30db\u309a") ),
        // japanese zenkaku katakana small "ke"
        arguments( "\u30f6", new NormalizeResult("\u30f6", "\u30f6", "\u30f6", "\u30f6") ),
        // japanese zenkaku katakana "ka" + japanese han-dakuten
        arguments( "\u30ab\u309a", new NormalizeResult("\u30ab\u309a", "\u30ab\u309a", "\u30ab\u309a", "\u30ab\u309a") ),
        // japanese zenkaku katakana "to" + japanese han-dakuten
        arguments( "\u30c8\u309a", new NormalizeResult("\u30c8\u309a", "\u30c8\u309a", "\u30c8\u309a", "\u30c8\u309a") ),
        // japanese half-width kana "ha"
        arguments( "\uff8a", new NormalizeResult("\uff8a", "\uff8a", "\u30cf", "\u30cf") ),
        // japanese half-width kana "vu"
        arguments( "\uff73\uff9e", new NormalizeResult("\uff73\uff9e", "\uff73\uff9e", "\u30f4", "\u30a6\u3099") ),
        // japanese half-width kana "pa"
        arguments( "\uff8a\uff9f", new NormalizeResult("\uff8a\uff9f", "\uff8a\uff9f", "\u30d1", "\u30cf\u309a") ),
        // latin "A", latin "a", zenkaku "A", zenkaku "a", latin number one, zenkaku number one
        arguments(
                "Aa\uff21\uff41\u0031\uff11",
                new NormalizeResult("Aa\uff21\uff41\u0031\uff11", "Aa\uff21\uff41\u0031\uff11", "AaAa11",
                        "AaAa11") ),
        // maru-number 1, maru-number 1(2), maru-number 27
        arguments(
                "\u2460\u2780\u3251",
                new NormalizeResult("\u2460\u2780\u3251", "\u2460\u2780\u3251", "1\u2780\u0032\u0031",
                        "1\u2780\u0032\u0031") ),
        // zenkaku marks
        arguments(
                "\\uff01\uff02\uff03\uff05\uff06\uff07\uff08\uff09\uff0a\uff0b\uff0c\uff0c\uff0e\uff0f\uff1a\uff1b\uff1c\uff1d\uff1e\uff1f\uff20\uff3b\uff3c\uff3d\uff3e\uff3f\uff40\uff5b\uff5c\uff5d\uff5e",
                new NormalizeResult(
                        "\\\u0075\u0066\u0066\u0030\u0031\uff02\uff03\uff05\uff06\uff07\uff08\uff09\uff0a\uff0b\uff0c\uff0c\uff0e\uff0f\uff1a\uff1b\uff1c\uff1d\uff1e\uff1f\uff20\uff3b\uff3c\uff3d\uff3e\uff3f\uff40\uff5b\uff5c\uff5d\uff5e",
                        "\\\u0075\u0066\u0066\u0030\u0031\uff02\uff03\uff05\uff06\uff07\uff08\uff09\uff0a\uff0b\uff0c\uff0c\uff0e\uff0f\uff1a\uff1b\uff1c\uff1d\uff1e\uff1f\uff20\uff3b\uff3c\uff3d\uff3e\uff3f\uff40\uff5b\uff5c\uff5d\uff5e",
                        "\\\u0075\u0066\u0066\u0030\u0031"
                                + '"'
                                + "\u0023\u0025\u0026\u0027\u0028\u0029\u002a\u002b\u002c\u002c\u002e\u002f\u003a\u003b\u003c\u003d\u003e\u003f\u0040\u005b\\\u005d\u005e\u005f\u0060\u007b\u007c\u007d\u007e",
                        "\\\u0075\u0066\u0066\u0030\u0031"
                                + '"'
                                + "\u0023\u0025\u0026\u0027\u0028\u0029\u002a\u002b\u002c\u002c\u002e\u002f\u003a\u003b\u003c\u003d\u003e\u003f\u0040\u005b\\\u005d\u005e\u005f\u0060\u007b\u007c\u007d\u007e") ),
        // latin "A" with acute
        arguments( "\u00c1", new NormalizeResult("\u00c1", "\u0041\u0301", "\u00c1", "\u0041\u0301") ),
        // latin "A" with ring above
        arguments( "\u00c5", new NormalizeResult("\u00c5", "\u0041\u030a", "\u00c5", "\u0041\u030a") ),
        // latin "C" with cedilla
        arguments( "\u00c7", new NormalizeResult("\u00c7", "\u0043\u0327", "\u00c7", "\u0043\u0327") ),
        // latin "E" with grave
        arguments( "\u00c8", new NormalizeResult("\u00c8", "\u0045\u0300", "\u00c8", "\u0045\u0300") ),
        // latin "n" with tilde
        arguments( "\u00f1", new NormalizeResult("\u00f1", "\u006e\u0303", "\u00f1", "\u006e\u0303") ),
        // latin "o" with diaeresis
        arguments( "\u00f6", new NormalizeResult("\u00f6", "\u006f\u0308", "\u00f6", "\u006f\u0308") ),
        // latin "o" with diaeresis
        arguments( "\u00f6", new NormalizeResult("\u00f6", "\u006f\u0308", "\u00f6", "\u006f\u0308") ),
        // latin "u" with circumflex
        arguments( "\u00fb", new NormalizeResult("\u00fb", "\u0075\u0302", "\u00fb", "\u0075\u0302") ),
        // latin "AE"
        arguments( "\u00c6", new NormalizeResult("\u00c6", "\u00c6", "\u00c6", "\u00c6") ),
        // latin "ETH"
        arguments( "\u00d0", new NormalizeResult("\u00d0", "\u00d0", "\u00d0", "\u00d0") ),
        // latin "Estset"
        arguments( "\u00df", new NormalizeResult("\u00df", "\u00df", "\u00df", "\u00df") ),
        // latin "o" with stroke
        arguments( "\u00f8", new NormalizeResult("\u00f8", "\u00f8", "\u00f8", "\u00f8") ),
        // latin small thorn
        arguments( "\u00fe", new NormalizeResult("\u00fe", "\u00fe", "\u00fe", "\u00fe") ),
        // latin "B", ring above
        arguments( "\u0042\u030a", new NormalizeResult("\u0042\u030a", "\u0042\u030a", "\u0042\u030a", "\u0042\u030a") ),
        // latin "ae", grave
        arguments( "\u00e6\u0300", new NormalizeResult("\u00e6\u0300", "\u00e6\u0300", "\u00e6\u0300", "\u00e6\u0300") ),
        // acute
        arguments( "\u00b4", new NormalizeResult("\u00b4", "\u00b4", "\u0020\u0301", "\u0020\u0301") ),
        // diaeresis
        arguments( "\u00a8", new NormalizeResult("\u00a8", "\u00a8", "\u0020\u0308", "\u0020\u0308") ),
        // overline
        arguments( "\u203e", new NormalizeResult("\u203e", "\u203e", "\u0020\u0305", "\u0020\u0305") ),
        // macron
        arguments( "\u00af", new NormalizeResult("\u00af", "\u00af", "\u0020\u0304", "\u0020\u0304") ),
        // zenkaku macron
        arguments( "\uffe3", new NormalizeResult("\uffe3", "\uffe3", "\u0020\u0304", "\u0020\u0304") ),
        // cedilla
        arguments( "\u00b8", new NormalizeResult("\u00b8", "\u00b8", "\u0020\u0327", "\u0020\u0327") ),
        // japanese dakuten
        arguments( "\u309b", new NormalizeResult("\u309b", "\u309b", "\u0020\u3099", "\u0020\u3099") ),
        // japanese han-dakuten
        arguments( "\u309c", new NormalizeResult("\u309c", "\u309c", "\u0020\u309a", "\u0020\u309a") ),
        // japanese hiragana "ha", japanese dakuten
        arguments( "\u306f\u309b",
                new NormalizeResult("\u306f\u309b", "\u306f\u309b", "\u306f\u0020\u3099",
                        "\u306f\u0020\u3099") ),
        // japanese zenkaku katakana "ho", japanese han-dakuten
        arguments( "\u30db\u309c",
                new NormalizeResult("\u30db\u309c", "\u30db\u309c", "\u30db\u0020\u309a",
                        "\u30db\u0020\u309a") ),
        // japanese half-width dakuten
        arguments( "\uff9e", new NormalizeResult("\uff9e", "\uff9e", "\u3099", "\u3099") ),
        // japanese half-width han-dakuten
        arguments( "\uff9e", new NormalizeResult("\uff9e", "\uff9e", "\u3099", "\u3099") ),
        // japanese hiragana "ha", half-width dakuten
        arguments( "\u306f\uff9e", new NormalizeResult("\u306f\uff9e", "\u306f\uff9e", "\u3070", "\u306f\u3099") ),
        // japanese katakana "ho", half-width han-dakuten
        arguments( "\u30db\uff9f", new NormalizeResult("\u30db\uff9f", "\u30db\uff9f", "\u30dd", "\u30db\u309a") ),
        // latin circumflex, underline, grave, tilde, japanese zenkaku circumflex, underline, grave
        arguments(
                "\u005e\u005f\u0060\u007e\uff3e\uff3f\uff40",
                new NormalizeResult("\u005e\u005f\u0060\u007e\uff3e\uff3f\uff40",
                        "\u005e\u005f\u0060\u007e\uff3e\uff3f\uff40",
                        "\u005e\u005f\u0060\u007e\u005e\u005f\u0060",
                        "\u005e\u005f\u0060\u007e\u005e\u005f\u0060") ),
        // japanese half-width ideographic full stop
        arguments( "\uff61", new NormalizeResult("\uff61", "\uff61", "\u3002", "\u3002") ),
        // japanese half-width left corner bracket
        arguments( "\uff62", new NormalizeResult("\uff62", "\uff62", "\u300c", "\u300c") ),
        // japanese half-width right corner bracket
        arguments( "\uff63", new NormalizeResult("\uff63", "\uff63", "\u300d", "\u300d") ),
        // japanese half-width ideographic comma
        arguments( "\uff64", new NormalizeResult("\uff64", "\uff64", "\u3001", "\u3001") ),
        // japanese half-width katakana middle dot
        arguments( "\uff65", new NormalizeResult("\uff65", "\uff65", "\u30fb", "\u30fb") ),
        // japanese fullwidth apostrophe
        arguments( "\uff07", new NormalizeResult("\uff07", "\uff07", "\u0027", "\u0027") ),
        // japanese fullwidth quotation mark
        arguments( "\uff02", new NormalizeResult("\uff02", "\uff02", "\"", "\"") ),
        // japanese fullwidth broken bar
        arguments( "\uffe4", new NormalizeResult("\uffe4", "\uffe4", "\u00a6", "\u00a6") ),
        // fullwidth hyphen-minus
        arguments( "\uff0d", new NormalizeResult("\uff0d", "\uff0d", "\u002d", "\u002d") ),
        // minus sign
        arguments( "\u2212", new NormalizeResult("\u2212", "\u2212", "\u2212", "\u2212") ),
        // fullwidth tilde
        arguments( "\uff5e", new NormalizeResult("\uff5e", "\uff5e", "\u007e", "\u007e") ),
        // wave dash
        arguments( "\u301c", new NormalizeResult("\u301c", "\u301c", "\u301c", "\u301c") ),
        // fullwidth cent sign
        arguments( "\uffe0", new NormalizeResult("\uffe0", "\uffe0", "\u00a2", "\u00a2") ),
        // fullwidth pound sign
        arguments( "\uffe1", new NormalizeResult("\uffe1", "\uffe1", "\u00a3", "\u00a3") ),
        // fullwidth not sign
        arguments( "\uffe2", new NormalizeResult("\uffe2", "\uffe2", "\u00ac", "\u00ac") ),
        // fullwidth yen sign
        arguments( "\uffe5", new NormalizeResult("\uffe5", "\uffe5", "\u00a5", "\u00a5") ),
        // hiragana voiced iteration mark
        arguments( "\u309e", new NormalizeResult("\u309e", "\u309d\u3099", "\u309e", "\u309d\u3099") ),
        // katakana voiced iteration mark
        arguments( "\u30fe", new NormalizeResult("\u30fe", "\u30fd\u3099", "\u30fe", "\u30fd\u3099") ),
        // not equal to
        arguments( "\u2260", new NormalizeResult("\u2260", "\u003d\u0338", "\u2260", "\u003d\u0338") ),
        // angstrom sign
        arguments( "\u212b", new NormalizeResult("\u00c5", "\u0041\u030a", "\u00c5", "\u0041\u030a") ),
        // ideographic space, two dot leader
        arguments( "\u3000\u2025",
                new NormalizeResult("\u3000\u2025", "\u3000\u2025", "\u0020\u002e\u002e",
                        "\u0020\u002e\u002e") ),
        // horizontal ellipsis
        arguments( "\u2026", new NormalizeResult("\u2026", "\u2026", "\u002e\u002e\u002e", "\u002e\u002e\u002e") ),
        // double prime
        arguments( "\u2033", new NormalizeResult("\u2033", "\u2033", "\u2032\u2032", "\u2032\u2032") ),
        // degree celsius
        arguments( "\u2103", new NormalizeResult("\u2103", "\u2103", "\u00b0\u0043", "\u00b0\u0043") ),
        // double integral
        arguments( "\u222c", new NormalizeResult("\u222c", "\u222c", "\u222b\u222b", "\u222b\u222b") ),
        // roman numeral three
        arguments( "\u2162", new NormalizeResult("\u2162", "\u2162", "\u0049\u0049\u0049", "\u0049\u0049\u0049") ),
        // small roman numeral seven
        arguments( "\u2176", new NormalizeResult("\u2176", "\u2176", "\u0076\u0069\u0069", "\u0076\u0069\u0069") ),
        // japanese squre miri
        arguments( "\u3349", new NormalizeResult("\u3349", "\u3349", "\u30df\u30ea", "\u30df\u30ea") ),
        // japanese squre doru
        arguments( "\u3326", new NormalizeResult("\u3326", "\u3326", "\u30c9\u30eb", "\u30c8\u3099\u30eb") ),
        // japanese squre m squared
        arguments( "\u33a1", new NormalizeResult("\u33a1", "\u33a1", "\u006d\u0032", "\u006d\u0032") ),
        // japanese sqare era name heisei
        arguments( "\u337b", new NormalizeResult("\u337b", "\u337b", "\u5e73\u6210", "\u5e73\u6210") ),
        // numero sign
        arguments( "\u2116", new NormalizeResult("\u2116", "\u2116", "\u004e\u006f", "\u004e\u006f") ),
        // squre kk
        arguments( "\u33cd", new NormalizeResult("\u33cd", "\u33cd", "\u004b\u004b", "\u004b\u004b") ),
        // telephone sign
        arguments( "\u2121", new NormalizeResult("\u2121", "\u2121", "\u0054\u0045\u004c", "\u0054\u0045\u004c") ),
        // japanese circled ideograph high
        arguments( "\u32a4", new NormalizeResult("\u32a4", "\u32a4", "\u4e0a", "\u4e0a") ),
        // japanese parenthesized ideograph stock
        arguments( "\u3231", new NormalizeResult("\u3231", "\u3231", "\u0028\u682a\u0029", "\u0028\u682a\u0029") ),
        // non-breakable space
        arguments( "\u00a0", new NormalizeResult("\u00a0", "\u00a0", "\u0020", "\u0020") ),
        // superscript one
        arguments( "\u00b9", new NormalizeResult("\u00b9", "\u00b9", "\u0031", "\u0031") ),
        // superscript two
        arguments( "\u00b2", new NormalizeResult("\u00b2", "\u00b2", "\u0032", "\u0032") ),
        // superscript three
        arguments( "\u00b3", new NormalizeResult("\u00b3", "\u00b3", "\u0033", "\u0033") ),
        // vulgar faction one quarter
        arguments( "\u00bc", new NormalizeResult("\u00bc", "\u00bc", "\u0031\u2044\u0034", "\u0031\u2044\u0034") ),
        // vulgar faction one half
        arguments( "\u00bd", new NormalizeResult("\u00bd", "\u00bd", "\u0031\u2044\u0032", "\u0031\u2044\u0032") ),
        // vulgar faction three quarters
        arguments( "\u00be", new NormalizeResult("\u00be", "\u00be", "\u0033\u2044\u0034", "\u0033\u2044\u0034") ),
        arguments( "", new NormalizeResult("", "", "", "") )
        // @formatter:on
        );
    }

    @ParameterizedTest
    @MethodSource("provideNomalizeSamples")
    public void testNormalizeSamples(String src, NormalizeResult expected) {
        assertThat(normalize(src)).isEqualTo(expected);
    }
}
