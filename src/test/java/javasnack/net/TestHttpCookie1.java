package javasnack.net;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.HttpCookie;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TestHttpCookie1 {

    @Test
    public void testDomainMatches() throws URISyntaxException {
        assertFalse(HttpCookie.domainMatches("", "aaa.bbb.com"));
        assertTrue(HttpCookie.domainMatches("aaa.bbb.com", "aaa.bbb.com"));

        assertFalse(HttpCookie.domainMatches("com", "com"));
        assertFalse(HttpCookie.domainMatches("com", "xxxcom"));
        assertFalse(HttpCookie.domainMatches("com", "aaa.com"));
        assertFalse(HttpCookie.domainMatches(".com", "com"));
        assertFalse(HttpCookie.domainMatches(".com", "xxxcom"));
        assertFalse(HttpCookie.domainMatches(".com", "aaa.com"));
        assertFalse(HttpCookie.domainMatches(".com", "xxxaaa.com"));

        assertFalse(HttpCookie.domainMatches("example", "example"));
        assertFalse(HttpCookie.domainMatches("example", "example.local"));

        assertTrue(HttpCookie.domainMatches(".local", "example")); // see javadoc
        assertTrue(HttpCookie.domainMatches(".local", "example.local")); // see javadoc

        assertTrue(HttpCookie.domainMatches("bbb.com", "bbb.com"));
        assertTrue(HttpCookie.domainMatches("bbb.com", "xxxbbb.com")); // WOW!!
        assertFalse(HttpCookie.domainMatches("bbb.com", "aaa.bbb.com"));

        assertTrue(HttpCookie.domainMatches(".bbb.com", "bbb.com"));
        assertFalse(HttpCookie.domainMatches(".bbb.com", "xxxbbb.com"));
        assertTrue(HttpCookie.domainMatches(".bbb.com", "aaa.bbb.com"));
        assertFalse(HttpCookie.domainMatches(".bbb.com", "xxx.aaa.bbb.com"));
    }

    /* see: https://developer.mozilla.org/ja/docs/Web/HTTP/Headers/Set-Cookie
     * see: https://developer.mozilla.org/ja/docs/Web/HTTP/Headers/Date
     * see: https://docs.oracle.com/javase/jp/11/docs/api/java.base/java/net/HttpCookie.html
     */
    @Test
    public void testParseSingleSetCookie() {
        assertThatThrownBy(() -> {
            HttpCookie.parse("");
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Empty cookie header string");

        assertThatThrownBy(() -> {
            HttpCookie.parse("xxxxx");
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid cookie name-value pair");

        assertThatThrownBy(() -> {
            HttpCookie.parse("xxx: yyy");
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid cookie name-value pair");

        List<HttpCookie> cookies = HttpCookie.parse("Set-Cookie: n=v");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).getComment()).isNull();
        assertThat(cookies.get(0).getDomain()).isNull();
        assertThat(cookies.get(0).getMaxAge()).isEqualTo(-1);
        assertThat(cookies.get(0).getPath()).isNull();
        assertThat(cookies.get(0).getPortlist()).isNull();
        assertThat(cookies.get(0).getSecure()).isFalse();
        assertThat(cookies.get(0).getVersion()).isEqualTo(0);
        assertThat(cookies.get(0).hasExpired()).isFalse();
        assertThat(cookies.get(0).isHttpOnly()).isFalse();

        cookies = HttpCookie.parse("Set-Cookie: n%'\"[z]=v%'\"[z]");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n%'\"[z]");
        assertThat(cookies.get(0).getValue()).isEqualTo("v%'\"[z]");

        cookies = HttpCookie.parse("Set-Cookie: n=v; domain=xxx");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).getDomain()).isEqualTo("xxx");

        cookies = HttpCookie.parse("Set-Cookie: n=v; path=xxx");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).getPath()).isEqualTo("xxx");

        cookies = HttpCookie.parse("Set-Cookie: n=v; secure");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).getSecure()).isTrue();

        cookies = HttpCookie.parse("Set-Cookie: n=v; httponly");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).isHttpOnly()).isTrue();

        cookies = HttpCookie.parse("Set-Cookie: n=v; max-age=1");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).getMaxAge()).isEqualTo(1);
        assertThat(cookies.get(0).hasExpired()).isFalse();

        cookies = HttpCookie.parse("Set-Cookie: n=v; max-age=0");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).getMaxAge()).isEqualTo(0);
        assertThat(cookies.get(0).hasExpired()).isTrue();

        cookies = HttpCookie.parse("Set-Cookie: n=v; max-age=-1");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).getMaxAge()).isEqualTo(-1);
        /* in MDN, zero "or" negative number will expire the cookie IMMEDIATELY.
         * see: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie
         * 
         * but in jdk, negative number treated as 
         * "this cookie should be discarded when user agent is to be closed, 
         *  but it is not expired."
         */
        assertThat(cookies.get(0).hasExpired()).isFalse();

        cookies = HttpCookie.parse("Set-Cookie: n=v; expires=Thu, 01 Jan 1970 01:02:03 GMT");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).hasExpired()).isTrue();

        final OffsetDateTime now0 = OffsetDateTime.now(ZoneId.of("GMT"));
        final OffsetDateTime past0 = now0.minusSeconds(10);
        cookies = HttpCookie.parse("Set-Cookie: n=v; expires=" + past0.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).hasExpired()).isTrue();

        final OffsetDateTime future0 = now0.plusSeconds(10);
        cookies = HttpCookie.parse("Set-Cookie: n=v; expires=" + future0.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).hasExpired()).isFalse();

        final OffsetDateTime future1 = future0.withOffsetSameInstant(ZoneOffset.ofHours(1));
        cookies = HttpCookie.parse("Set-Cookie: n=v; expires=" + future1.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).hasExpired()).isTrue(); // can't parse expires except "GMT" offset.

        cookies = HttpCookie.parse(
                "Set-Cookie: n=v; DOMAIN=xxx; PATH=yyy; SECURE; HTTPONLY; MAX-AGE=123; EXPIRES=Thu, 01 Jan 1970 01:02:03 GMT");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).getDomain()).isEqualTo("xxx");
        assertThat(cookies.get(0).getPath()).isEqualTo("yyy");
        assertThat(cookies.get(0).getSecure()).isTrue();
        assertThat(cookies.get(0).isHttpOnly()).isTrue();
        assertThat(cookies.get(0).getMaxAge()).isEqualTo(123);
        assertThat(cookies.get(0).hasExpired()).isFalse();

        cookies = HttpCookie.parse(
                "Set-Cookie: n=v; EXPIRES=Thu, 01 Jan 1970 01:02:03 GMT; HTTPONLY; PATH=yyy; SECURE; DOMAIN=xxx");
        assertThat(cookies).hasSize(1);
        assertThat(cookies.get(0).getName()).isEqualTo("n");
        assertThat(cookies.get(0).getValue()).isEqualTo("v");
        assertThat(cookies.get(0).getDomain()).isEqualTo("xxx");
        assertThat(cookies.get(0).getPath()).isEqualTo("yyy");
        assertThat(cookies.get(0).getSecure()).isTrue();
        assertThat(cookies.get(0).isHttpOnly()).isTrue();
        assertThat(cookies.get(0).hasExpired()).isTrue();
    }
}
