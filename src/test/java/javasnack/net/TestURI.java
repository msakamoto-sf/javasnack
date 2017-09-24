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

import java.net.URI;
import java.net.URISyntaxException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * typical getter usage of {@link java.net.URI}
 */
public class TestURI {

    @Test
    public void testTypicalGetter() throws URISyntaxException {
        URI url0 = new URI("http://localhost/");
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), "http");
        assertEquals(url0.getSchemeSpecificPart(), "//localhost/");
        assertEquals(url0.getRawSchemeSpecificPart(), "//localhost/");
        assertEquals(url0.getAuthority(), "localhost");
        assertEquals(url0.getRawAuthority(), "localhost");
        assertEquals(url0.getHost(), "localhost");
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), "/");
        assertEquals(url0.getRawPath(), "/");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRawQuery(), null);
        assertEquals(url0.getFragment(), null);
        assertEquals(url0.getRawFragment(), null);
        assertEquals(url0.toASCIIString(), "http://localhost/");
        assertEquals(url0.toString(), "http://localhost/");

        String url1 = "xxx://username:password@localhost:8080/aaa/bbb;ccc;ddd/eee.txt;fff=ggg?q1=v1&q2=v2#ref123";
        url0 = new URI(url1);
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), "xxx");
        assertEquals(url0.getSchemeSpecificPart(),
                "//username:password@localhost:8080/aaa/bbb;ccc;ddd/eee.txt;fff=ggg?q1=v1&q2=v2");
        assertEquals(url0.getRawSchemeSpecificPart(),
                "//username:password@localhost:8080/aaa/bbb;ccc;ddd/eee.txt;fff=ggg?q1=v1&q2=v2");
        assertEquals(url0.getAuthority(), "username:password@localhost:8080");
        assertEquals(url0.getRawAuthority(), "username:password@localhost:8080");
        assertEquals(url0.getHost(), "localhost");
        assertEquals(url0.getUserInfo(), "username:password");
        assertEquals(url0.getRawUserInfo(), "username:password");
        assertEquals(url0.getPort(), 8080);
        assertEquals(url0.getPath(), "/aaa/bbb;ccc;ddd/eee.txt;fff=ggg");
        assertEquals(url0.getRawPath(), "/aaa/bbb;ccc;ddd/eee.txt;fff=ggg");
        assertEquals(url0.getQuery(), "q1=v1&q2=v2");
        assertEquals(url0.getRawQuery(), "q1=v1&q2=v2");
        assertEquals(url0.getFragment(), "ref123");
        assertEquals(url0.getRawFragment(), "ref123");
        assertEquals(url0.toASCIIString(), url1);
        assertEquals(url0.toString(), url1);

        String url2 = "xxx://user%20%2Fname:pass%20%2Fword@local%20host:8080/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg?q1=v1&q2%20%2F=v2#ref%2F%20123";
        url0 = new URI(url2);
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), "xxx");
        assertEquals(url0.getSchemeSpecificPart(),
                "//user /name:pass /word@local host:8080/aaa/bbb;ccc;ddd /eee.txt;fff=ggg?q1=v1&q2 /=v2");
        assertEquals(url0.getRawSchemeSpecificPart(),
                "//user%20%2Fname:pass%20%2Fword@local%20host:8080/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg?q1=v1&q2%20%2F=v2");
        assertEquals(url0.getAuthority(), "user /name:pass /word@local host:8080");
        assertEquals(url0.getRawAuthority(), "user%20%2Fname:pass%20%2Fword@local%20host:8080");
        assertEquals(url0.getHost(), null);
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), "/aaa/bbb;ccc;ddd /eee.txt;fff=ggg");
        assertEquals(url0.getRawPath(), "/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg");
        assertEquals(url0.getQuery(), "q1=v1&q2 /=v2");
        assertEquals(url0.getRawQuery(), "q1=v1&q2%20%2F=v2");
        assertEquals(url0.getFragment(), "ref/ 123");
        assertEquals(url0.getRawFragment(), "ref%2F%20123");
        assertEquals(url0.toASCIIString(), url2);
        assertEquals(url0.toString(), url2);

        url0 = new URI("");
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), null);
        assertEquals(url0.getSchemeSpecificPart(), "");
        assertEquals(url0.getRawSchemeSpecificPart(), "");
        assertEquals(url0.getAuthority(), null);
        assertEquals(url0.getRawAuthority(), null);
        assertEquals(url0.getHost(), null);
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), "");
        assertEquals(url0.getRawPath(), "");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRawQuery(), null);
        assertEquals(url0.getFragment(), null);
        assertEquals(url0.getRawFragment(), null);
        assertEquals(url0.toASCIIString(), "");
        assertEquals(url0.toString(), "");

        url0 = new URI("/");
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), null);
        assertEquals(url0.getSchemeSpecificPart(), "/");
        assertEquals(url0.getRawSchemeSpecificPart(), "/");
        assertEquals(url0.getAuthority(), null);
        assertEquals(url0.getRawAuthority(), null);
        assertEquals(url0.getHost(), null);
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), "/");
        assertEquals(url0.getRawPath(), "/");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRawQuery(), null);
        assertEquals(url0.getFragment(), null);
        assertEquals(url0.getRawFragment(), null);
        assertEquals(url0.toASCIIString(), "/");
        assertEquals(url0.toString(), "/");

        url0 = new URI("a");
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), null);
        assertEquals(url0.getSchemeSpecificPart(), "a");
        assertEquals(url0.getRawSchemeSpecificPart(), "a");
        assertEquals(url0.getAuthority(), null);
        assertEquals(url0.getRawAuthority(), null);
        assertEquals(url0.getHost(), null);
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), "a");
        assertEquals(url0.getRawPath(), "a");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRawQuery(), null);
        assertEquals(url0.getFragment(), null);
        assertEquals(url0.getRawFragment(), null);
        assertEquals(url0.toASCIIString(), "a");
        assertEquals(url0.toString(), "a");

        url0 = new URI("file:///");
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), "file");
        assertEquals(url0.getSchemeSpecificPart(), "///");
        assertEquals(url0.getRawSchemeSpecificPart(), "///");
        assertEquals(url0.getAuthority(), null);
        assertEquals(url0.getRawAuthority(), null);
        assertEquals(url0.getHost(), null);
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), "/");
        assertEquals(url0.getRawPath(), "/");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRawQuery(), null);
        assertEquals(url0.getFragment(), null);
        assertEquals(url0.getRawFragment(), null);
        assertEquals(url0.toASCIIString(), "file:///");
        assertEquals(url0.toString(), "file:///");

        url0 = new URI("file:///aaa/bbb/ccc.txt");
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), "file");
        assertEquals(url0.getSchemeSpecificPart(), "///aaa/bbb/ccc.txt");
        assertEquals(url0.getRawSchemeSpecificPart(), "///aaa/bbb/ccc.txt");
        assertEquals(url0.getAuthority(), null);
        assertEquals(url0.getRawAuthority(), null);
        assertEquals(url0.getHost(), null);
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getRawPath(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRawQuery(), null);
        assertEquals(url0.getFragment(), null);
        assertEquals(url0.getRawFragment(), null);
        assertEquals(url0.toASCIIString(), "file:///aaa/bbb/ccc.txt");
        assertEquals(url0.toString(), "file:///aaa/bbb/ccc.txt");

        url0 = new URI("file://C:/aaa/bbb/ccc.txt");
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), "file");
        assertEquals(url0.getSchemeSpecificPart(), "//C:/aaa/bbb/ccc.txt");
        assertEquals(url0.getRawSchemeSpecificPart(), "//C:/aaa/bbb/ccc.txt");
        assertEquals(url0.getAuthority(), "C:");
        assertEquals(url0.getRawAuthority(), "C:");
        assertEquals(url0.getHost(), "C");
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getRawPath(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRawQuery(), null);
        assertEquals(url0.getFragment(), null);
        assertEquals(url0.getRawFragment(), null);
        assertEquals(url0.toASCIIString(), "file://C:/aaa/bbb/ccc.txt");
        assertEquals(url0.toString(), "file://C:/aaa/bbb/ccc.txt");

        url0 = new URI("xxx:yyy");
        assertEquals(url0.isOpaque(), true);
        assertEquals(url0.getScheme(), "xxx");
        assertEquals(url0.getSchemeSpecificPart(), "yyy");
        assertEquals(url0.getRawSchemeSpecificPart(), "yyy");
        assertEquals(url0.getAuthority(), null);
        assertEquals(url0.getRawAuthority(), null);
        assertEquals(url0.getHost(), null);
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), null);
        assertEquals(url0.getRawPath(), null);
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRawQuery(), null);
        assertEquals(url0.getFragment(), null);
        assertEquals(url0.getRawFragment(), null);
        assertEquals(url0.toASCIIString(), "xxx:yyy");
        assertEquals(url0.toString(), "xxx:yyy");

        url0 = new URI("ftp://localhost/aaa/bbb/ccc.txt");
        assertEquals(url0.isOpaque(), false);
        assertEquals(url0.getScheme(), "ftp");
        assertEquals(url0.getSchemeSpecificPart(), "//localhost/aaa/bbb/ccc.txt");
        assertEquals(url0.getRawSchemeSpecificPart(), "//localhost/aaa/bbb/ccc.txt");
        assertEquals(url0.getAuthority(), "localhost");
        assertEquals(url0.getRawAuthority(), "localhost");
        assertEquals(url0.getHost(), "localhost");
        assertEquals(url0.getUserInfo(), null);
        assertEquals(url0.getRawUserInfo(), null);
        assertEquals(url0.getPort(), -1);
        assertEquals(url0.getPath(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getRawPath(), "/aaa/bbb/ccc.txt");
        assertEquals(url0.getQuery(), null);
        assertEquals(url0.getRawQuery(), null);
        assertEquals(url0.getFragment(), null);
        assertEquals(url0.getRawFragment(), null);
        assertEquals(url0.toASCIIString(), "ftp://localhost/aaa/bbb/ccc.txt");
        assertEquals(url0.toString(), "ftp://localhost/aaa/bbb/ccc.txt");
    }

    @DataProvider(name = "uriSyntaxErrors")
    public Object[][] provideURISyntaxErrors() {
        return new Object[][] {
            // @formatter:off
            { "C:\\aaa\\bbb\\ccc.html" },
            // @formatter:on
        };
    }

    @Test(dataProvider = "uriSyntaxErrors", expectedExceptions = { URISyntaxException.class })
    public void testURISyntaxExceptionDemo(String uristr) throws URISyntaxException {
        new URI(uristr);
        Assert.fail("not reached here.");
    }
}
