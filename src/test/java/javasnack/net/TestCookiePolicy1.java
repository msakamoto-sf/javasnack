package javasnack.net;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;

import org.testng.annotations.Test;

public class TestCookiePolicy1 {

    @Test
    public void testAcceptOriginServer() throws URISyntaxException {
        final CookiePolicy cp = CookiePolicy.ACCEPT_ORIGINAL_SERVER;
        final URI setcookieUri = new URI("http://www.example.com/");
        HttpCookie c0 = new HttpCookie("c0", "v0");
        // reject empty domain cookie (actually, default CookieManager set requested uri as default)
        assertFalse(cp.shouldAccept(setcookieUri, c0));

        // accept identical domain cookie
        c0.setDomain("www.example.com");
        assertTrue(cp.shouldAccept(setcookieUri, c0));

        // reject parent or sub domain cookie
        c0.setDomain("example.com");
        assertFalse(cp.shouldAccept(setcookieUri, c0));
        c0.setDomain("aaa.www.example.com");
        assertFalse(cp.shouldAccept(setcookieUri, c0));

        // reject outer domain cookie
        c0.setDomain("foo.bar.baz");
        assertFalse(cp.shouldAccept(setcookieUri, c0));
    }

    @Test
    public void testAcceptAll() throws URISyntaxException {
        final CookiePolicy cp = CookiePolicy.ACCEPT_ALL;
        final URI setcookieUri = new URI("http://www.example.com/");
        HttpCookie c0 = new HttpCookie("c0", "v0");
        // accept all
        assertTrue(cp.shouldAccept(setcookieUri, c0));

        c0.setDomain("www.example.com");
        assertTrue(cp.shouldAccept(setcookieUri, c0));

        c0.setDomain("example.com");
        assertTrue(cp.shouldAccept(setcookieUri, c0));
        c0.setDomain("aaa.www.example.com");
        assertTrue(cp.shouldAccept(setcookieUri, c0));

        c0.setDomain("foo.bar.baz");
        assertTrue(cp.shouldAccept(setcookieUri, c0));
    }

    @Test
    public void testAcceptNone() throws URISyntaxException {
        final CookiePolicy cp = CookiePolicy.ACCEPT_NONE;
        final URI setcookieUri = new URI("http://www.example.com/");
        HttpCookie c0 = new HttpCookie("c0", "v0");
        // reject all
        assertFalse(cp.shouldAccept(setcookieUri, c0));

        c0.setDomain("www.example.com");
        assertFalse(cp.shouldAccept(setcookieUri, c0));

        c0.setDomain("example.com");
        assertFalse(cp.shouldAccept(setcookieUri, c0));
        c0.setDomain("aaa.www.example.com");
        assertFalse(cp.shouldAccept(setcookieUri, c0));

        c0.setDomain("foo.bar.baz");
        assertFalse(cp.shouldAccept(setcookieUri, c0));
    }
}
