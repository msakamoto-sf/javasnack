/*
 * Copyright 2017 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.net;

import static org.testng.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * typical getter usage of {@link java.net.URL}
 */
public class TestURL {

    @Test
    public void testTypicalGetter() throws MalformedURLException {
        URL url0 = new URL("http://localhost/");
        assertEquals(url0.getProtocol(), "http");
        assertEquals(url0.getAuthority(), "localhost");
        assertEquals(url0.getHost(), "localhost");
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getDefaultPort(), 80);
        assertEquals(url0.getFile(), "/");
        assertEquals(url0.getPath(), "/");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRef(), null);
        assertEquals(url0.toExternalForm(), "http://localhost/");

        url0 = new URL("https://localhost/");
        assertEquals(url0.getProtocol(), "https");
        assertEquals(url0.getAuthority(), "localhost");
        assertEquals(url0.getHost(), "localhost");
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getDefaultPort(), 443);
        assertEquals(url0.getFile(), "/");
        assertEquals(url0.getPath(), "/");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRef(), null);
        assertEquals(url0.toExternalForm(), "https://localhost/");

        String url2 = "http://username:password@localhost:8080/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg?q1=v1&q2%20%2F=v2#ref123";
        url0 = new URL(url2);
        assertEquals(url0.getProtocol(), "http");
        assertEquals(url0.getAuthority(), "username:password@localhost:8080");
        assertEquals(url0.getHost(), "localhost");
        assertEquals(url0.getUserInfo(), "username:password");
        assertEquals(url0.getPort(), 8080);
        assertEquals(url0.getDefaultPort(), 80);
        assertEquals(url0.getFile(), "/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg?q1=v1&q2%20%2F=v2");
        assertEquals(url0.getPath(), "/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg");
        assertEquals(url0.getQuery(), "q1=v1&q2%20%2F=v2");
        assertEquals(url0.getRef(), "ref123");
        assertEquals(url0.toExternalForm(), url2);

        url0 = new URL("ftp://localhost/aaa/bbb/ccc.txt");
        assertEquals(url0.getProtocol(), "ftp");
        assertEquals(url0.getAuthority(), "localhost");
        assertEquals(url0.getHost(), "localhost");
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getDefaultPort(), 21);
        assertEquals(url0.getFile(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getPath(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRef(), null);
        assertEquals(url0.toExternalForm(), "ftp://localhost/aaa/bbb/ccc.txt");

        url0 = new URL("file:///aaa/bbb/ccc.txt");
        assertEquals(url0.getProtocol(), "file");
        assertEquals(url0.getAuthority(), "");
        assertEquals(url0.getHost(), "");
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getDefaultPort(), -1);
        assertEquals(url0.getFile(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getPath(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRef(), null);
        assertEquals(url0.toExternalForm(), "file:/aaa/bbb/ccc.txt");
        assertEquals(url0.toString(), "file:/aaa/bbb/ccc.txt");

        url0 = new URL("file:/aaa/bbb/ccc.txt");
        assertEquals(url0.getProtocol(), "file");
        assertEquals(url0.getAuthority(), null); // NOTE : compare to "file:///" constructor.
        assertEquals(url0.getHost(), "");
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getDefaultPort(), -1);
        assertEquals(url0.getFile(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getPath(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRef(), null);
        assertEquals(url0.toExternalForm(), "file:/aaa/bbb/ccc.txt");
        assertEquals(url0.toString(), "file:/aaa/bbb/ccc.txt");
    }

    @DataProvider(name = "marlformedUrls")
    public Object[][] provideMalformedUrls() {
        return new Object[][] {
            // @formatter:off
            { "" },
            { "/" },
            { "/aaa/bbb/ccc.html" },
            { "C:\\aaa\\bbb\\ccc.html" },
            { "xxx://localhost/aaa" },
            // @formatter:on
        };
    }

    @Test(dataProvider = "marlformedUrls", expectedExceptions = { MalformedURLException.class })
    public void testMalformedURLExceptionDemo(String urlstr) throws MalformedURLException {
        new URL(urlstr);
        Assert.fail("not reached here.");
    }
}
