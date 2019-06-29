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
package javasnack.net;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestCookieManager1 {

    @Test
    public void testTypicalUsecase1() throws URISyntaxException, IOException {
        CookieHandler ch = new CookieManager();
        Map<String, List<String>> responseHeaders = new HashMap<>();
        responseHeaders.put(
                "Set-Cookie",
                Arrays.asList(
                        "Set-Cookie: c1=v1",
                        "Set-Cookie: c2=v2; path=/aaa",
                        "Set-Cookie: c3=v3; path=/aaa/bbb",
                        "Set-Cookie: c4=v4; path=/xxx",
                        "Set-Cookie: c5=v5; path=/aaa; secure",
                        "Set-Cookie: c6=v6; path=/aaa/bbb; secure",
                        "Set-Cookie: c7=v7; path=/xxx; secure",
                        "Set-Cookie: c8=v8; httpOnly"));
        URI srcUri = new URI("https://www.example.com/aaa/bbb/ccc.html");
        ch.put(srcUri, responseHeaders);

        URI dstUri = new URI("http://www.example.com/aaa/bbb/ccc.html");
        Map<String, List<String>> retHeaders = ch.get(dstUri, new HashMap<>());
        List<String> retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(4);
        assertThat(retCookieStrings.get(0)).isEqualTo("c1=v1");
        assertThat(retCookieStrings.get(1)).isEqualTo("c2=v2");
        assertThat(retCookieStrings.get(2)).isEqualTo("c3=v3");
        assertThat(retCookieStrings.get(3)).isEqualTo("c8=v8");

        dstUri = new URI("https://www.example.com/aaa/bbb/ccc/ddd.html");
        retHeaders = ch.get(dstUri, new HashMap<>());
        retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(6);
        assertThat(retCookieStrings.get(0)).isEqualTo("c1=v1");
        assertThat(retCookieStrings.get(1)).isEqualTo("c2=v2");
        assertThat(retCookieStrings.get(2)).isEqualTo("c3=v3");
        assertThat(retCookieStrings.get(3)).isEqualTo("c5=v5");
        assertThat(retCookieStrings.get(4)).isEqualTo("c6=v6");
        assertThat(retCookieStrings.get(5)).isEqualTo("c8=v8");

        dstUri = new URI("https://www.example.com/aaa/bbb.html");
        retHeaders = ch.get(dstUri, new HashMap<>());
        retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(4);
        assertThat(retCookieStrings.get(0)).isEqualTo("c2=v2");
        assertThat(retCookieStrings.get(1)).isEqualTo("c3=v3");
        assertThat(retCookieStrings.get(2)).isEqualTo("c5=v5");
        assertThat(retCookieStrings.get(3)).isEqualTo("c6=v6");

        dstUri = new URI("https://www.example.com/");
        retHeaders = ch.get(dstUri, new HashMap<>());
        retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).isEmpty();
    }

    @Test
    public void testTypicalUsecase2() throws URISyntaxException, IOException {
        CookieHandler ch = new CookieManager();
        Map<String, List<String>> responseHeaders1 = new HashMap<>();
        responseHeaders1.put("Set-Cookie", Arrays.asList("Set-Cookie: c1=v1"));
        URI srcUri1 = new URI("http://www.example.com/");
        ch.put(srcUri1, responseHeaders1);
        Map<String, List<String>> responseHeaders2 = new HashMap<>();
        responseHeaders2.put("Set-Cookie", Arrays.asList("Set-Cookie: c2=v2"));
        URI srcUri2 = new URI("http://www.example.net/");
        ch.put(srcUri2, responseHeaders2);
        Map<String, List<String>> responseHeaders3 = new HashMap<>();
        responseHeaders3.put(
                "Set-Cookie",
                Arrays.asList(
                        "Set-Cookie: c3=v3",
                        "Set-Cookie: c4=v4; domain=example.net",
                        "Set-Cookie: c5=v5; domain=.example.net",
                        "Set-Cookie: c6=v6; domain=test.example.net"));
        URI srcUri3 = new URI("https://aaatest.example.net/");
        ch.put(srcUri3, responseHeaders3);

        URI dstUri = new URI("http://www.example.com/");
        Map<String, List<String>> retHeaders = ch.get(dstUri, new HashMap<>());
        List<String> retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(1);
        assertThat(retCookieStrings.get(0)).isEqualTo("c1=v1");

        dstUri = new URI("http://example.net/");
        retHeaders = ch.get(dstUri, new HashMap<>());
        retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(1);
        assertThat(retCookieStrings.get(0)).isEqualTo("c5=v5");

        dstUri = new URI("http://www.example.net/");
        retHeaders = ch.get(dstUri, new HashMap<>());
        retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(2);
        assertThat(retCookieStrings.get(0)).isEqualTo("c5=v5");
        assertThat(retCookieStrings.get(1)).isEqualTo("c2=v2");

        dstUri = new URI("http://test.example.net/");
        retHeaders = ch.get(dstUri, new HashMap<>());
        retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(2);
        assertThat(retCookieStrings.get(0)).isEqualTo("c5=v5");
        assertThat(retCookieStrings.get(1)).isEqualTo("c6=v6");

        dstUri = new URI("http://aaatest.example.net/");
        retHeaders = ch.get(dstUri, new HashMap<>());
        retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(3);
        assertThat(retCookieStrings.get(0)).isEqualTo("c3=v3");
        assertThat(retCookieStrings.get(1)).isEqualTo("c5=v5");
        assertThat(retCookieStrings.get(2)).isEqualTo("c6=v6");
    }

    @Test
    public void testTypicalUsecase3() throws URISyntaxException, IOException {
        CookieHandler ch = new CookieManager();
        Map<String, List<String>> responseHeaders1 = new HashMap<>();
        responseHeaders1.put("Set-Cookie", Arrays.asList("Set-Cookie: c1=v1"));
        URI srcUri1 = new URI("http://www.example.com/");
        ch.put(srcUri1, responseHeaders1);
        Map<String, List<String>> responseHeaders2 = new HashMap<>();
        responseHeaders2.put("Set-Cookie", Arrays.asList("Set-Cookie: c1=v2"));
        URI srcUri2 = new URI("https://www.example.com/");
        ch.put(srcUri2, responseHeaders2);
        Map<String, List<String>> responseHeaders3 = new HashMap<>();
        responseHeaders3.put("Set-Cookie", Arrays.asList("Set-Cookie: c1=v3"));
        URI srcUri3 = new URI("https://www.example.com:8443/");
        ch.put(srcUri3, responseHeaders3);

        URI dstUri = new URI("http://www.example.com/");
        Map<String, List<String>> retHeaders = ch.get(dstUri, new HashMap<>());
        List<String> retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(1);
        assertThat(retCookieStrings.get(0)).isEqualTo("c1=v3");

        dstUri = new URI("https://www.example.com/");
        retHeaders = ch.get(dstUri, new HashMap<>());
        retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(1);
        assertThat(retCookieStrings.get(0)).isEqualTo("c1=v3");

        dstUri = new URI("https://www.example.com:8443/");
        retHeaders = ch.get(dstUri, new HashMap<>());
        retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(1);
        assertThat(retCookieStrings.get(0)).isEqualTo("c1=v3");
    }

    @Test
    public void testDuplicatedSetCookie() throws URISyntaxException, IOException {
        CookieHandler ch = new CookieManager();
        Map<String, List<String>> responseHeaders = new HashMap<>();
        responseHeaders.put("Set-Cookie", Arrays.asList("Set-Cookie: c1=v1; secure", "Set-Cookie: c1=v2"));
        URI srcUri = new URI("https://www.example.com/");
        ch.put(srcUri, responseHeaders);

        URI dstUri = new URI("http://www.example.com/");
        Map<String, List<String>> retHeaders = ch.get(dstUri, new HashMap<>());
        List<String> retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(1);
        assertThat(retCookieStrings.get(0)).isEqualTo("c1=v2");
    }

    @Test
    public void testEncodedSetCookie() throws URISyntaxException, IOException {
        CookieHandler ch = new CookieManager();
        Map<String, List<String>> responseHeaders = new HashMap<>();
        responseHeaders.put(
                "Set-Cookie",
                Arrays.asList(
                        "Set-Cookie: c1%20%2A%2B%2F%3D%3Fx=v1%20%2A%2B%2F%3D%3Fx",
                        "Set-Cookie: c2%20%2A%2B%2F%3D%3Fx=v2%20%2A%2B%2F%3D%3Fx"));
        URI srcUri = new URI("http://localhost/");
        ch.put(srcUri, responseHeaders);

        URI dstUri = new URI("http://localhost/");
        Map<String, List<String>> retHeaders = ch.get(dstUri, new HashMap<>());
        List<String> retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(2);
        assertThat(retCookieStrings.get(0)).isEqualTo("c1%20%2A%2B%2F%3D%3Fx=v1%20%2A%2B%2F%3D%3Fx");
        assertThat(retCookieStrings.get(1)).isEqualTo("c2%20%2A%2B%2F%3D%3Fx=v2%20%2A%2B%2F%3D%3Fx");
    }

    @Test
    public void testSetCookieWithVersion1() throws URISyntaxException, IOException {
        CookieHandler ch = new CookieManager();
        Map<String, List<String>> responseHeaders = new HashMap<>();
        responseHeaders.put("Set-Cookie", Arrays.asList("Set-Cookie: c1=v1; Version=1"));
        URI srcUri = new URI("http://localhost/");
        ch.put(srcUri, responseHeaders);

        URI dstUri = new URI("http://localhost/");
        Map<String, List<String>> retHeaders = ch.get(dstUri, new HashMap<>());
        List<String> retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(2);
        assertThat(retCookieStrings.get(0)).isEqualTo("$Version=\"1\"");
        assertThat(retCookieStrings.get(1)).isEqualTo("c1=\"v1\";$Path=\"/\";$Domain=\"localhost.local\"");
    }

    @Test
    public void testSetCookieWithVersion1AndNonVersion() throws URISyntaxException, IOException {
        CookieHandler ch = new CookieManager();
        Map<String, List<String>> responseHeaders = new HashMap<>();
        responseHeaders.put("Set-Cookie", Arrays.asList("Set-Cookie: c1=v1; Version=1", "Set-Cookie: c2=v2"));
        URI srcUri = new URI("http://localhost/");
        ch.put(srcUri, responseHeaders);

        URI dstUri = new URI("http://localhost/");
        Map<String, List<String>> retHeaders = ch.get(dstUri, new HashMap<>());
        List<String> retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(3);
        assertThat(retCookieStrings.get(0)).isEqualTo("$Version=\"1\"");
        assertThat(retCookieStrings.get(1)).isEqualTo("c1=\"v1\";$Path=\"/\";$Domain=\"localhost.local\"");
        assertThat(retCookieStrings.get(2)).isEqualTo("c2=v2");
    }

    @Test
    public void testSetCookie2WithVersion1() throws URISyntaxException, IOException {
        CookieHandler ch = new CookieManager();
        Map<String, List<String>> responseHeaders = new HashMap<>();
        responseHeaders.put("Set-Cookie2", Arrays.asList("Set-Cookie2: c1=v1; Version=1"));
        URI srcUri = new URI("http://localhost/");
        ch.put(srcUri, responseHeaders);

        URI dstUri = new URI("http://localhost/");
        Map<String, List<String>> retHeaders = ch.get(dstUri, new HashMap<>());
        List<String> retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(2);
        assertThat(retCookieStrings.get(0)).isEqualTo("$Version=\"1\"");
        assertThat(retCookieStrings.get(1)).isEqualTo("c1=\"v1\";$Path=\"/\";$Domain=\"localhost.local\"");
    }

    /* see:
     * https://developer.mozilla.org/ja/docs/Web/HTTP/Headers/Set-Cookie2
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Cookie2
     */
    @Test
    public void testSetCookie2() throws URISyntaxException, IOException {
        CookieHandler ch = new CookieManager();
        Map<String, List<String>> responseHeaders = new HashMap<>();
        responseHeaders.put("Set-Cookie2", Arrays.asList("Set-Cookie2: c1=v1", "Set-Cookie2: c2=v2, c3=v3, c4=v4"));
        URI srcUri = new URI("http://localhost/");
        ch.put(srcUri, responseHeaders);

        URI dstUri = new URI("http://localhost/");
        Map<String, List<String>> retHeaders = ch.get(dstUri, new HashMap<>());
        List<String> retCookieStrings = retHeaders.get("Cookie");
        assertThat(retCookieStrings).hasSize(5);
        assertThat(retCookieStrings.get(0)).isEqualTo("$Version=\"1\"");
        assertThat(retCookieStrings.get(1)).isEqualTo("c1=\"v1\";$Path=\"/\";$Domain=\"localhost.local\"");
        assertThat(retCookieStrings.get(2)).isEqualTo("c2=\"v2\";$Path=\"/\";$Domain=\"localhost.local\"");
        assertThat(retCookieStrings.get(3)).isEqualTo("c3=\"v3\";$Path=\"/\";$Domain=\"localhost.local\"");
        assertThat(retCookieStrings.get(4)).isEqualTo("c4=\"v4\";$Path=\"/\";$Domain=\"localhost.local\"");
        retCookieStrings = retHeaders.get("Cookie2");
        assertThat(retCookieStrings).isNull();
    }
}
