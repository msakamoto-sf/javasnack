package javasnack.net;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.HttpCookie;
import java.net.URISyntaxException;

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
}
