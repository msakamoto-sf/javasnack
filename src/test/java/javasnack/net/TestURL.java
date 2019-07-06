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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * typical getter usage of {@link java.net.URL}
 */
public class TestURL {

    @Test
    public void testTypicalGetter() throws MalformedURLException {
        URL url0 = new URL("http://localhost/");
        assertThat(url0.getProtocol()).isEqualTo("http");
        assertThat(url0.getAuthority()).isEqualTo("localhost");
        assertThat(url0.getHost()).isEqualTo("localhost");
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getDefaultPort()).isEqualTo(80);
        assertThat(url0.getFile()).isEqualTo("/");
        assertThat(url0.getPath()).isEqualTo("/");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRef()).isEqualTo(null);
        assertThat(url0.toExternalForm()).isEqualTo("http://localhost/");

        url0 = new URL("https://localhost/");
        assertThat(url0.getProtocol()).isEqualTo("https");
        assertThat(url0.getAuthority()).isEqualTo("localhost");
        assertThat(url0.getHost()).isEqualTo("localhost");
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getDefaultPort()).isEqualTo(443);
        assertThat(url0.getFile()).isEqualTo("/");
        assertThat(url0.getPath()).isEqualTo("/");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRef()).isEqualTo(null);
        assertThat(url0.toExternalForm()).isEqualTo("https://localhost/");

        String url2 = "http://username:password@localhost:8080/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg?q1=v1&q2%20%2F=v2#ref123";
        url0 = new URL(url2);
        assertThat(url0.getProtocol()).isEqualTo("http");
        assertThat(url0.getAuthority()).isEqualTo("username:password@localhost:8080");
        assertThat(url0.getHost()).isEqualTo("localhost");
        assertThat(url0.getUserInfo()).isEqualTo("username:password");
        assertThat(url0.getPort()).isEqualTo(8080);
        assertThat(url0.getDefaultPort()).isEqualTo(80);
        assertThat(url0.getFile()).isEqualTo("/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg?q1=v1&q2%20%2F=v2");
        assertThat(url0.getPath()).isEqualTo("/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg");
        assertThat(url0.getQuery()).isEqualTo("q1=v1&q2%20%2F=v2");
        assertThat(url0.getRef()).isEqualTo("ref123");
        assertThat(url0.toExternalForm()).isEqualTo(url2);

        url0 = new URL("ftp://localhost/aaa/bbb/ccc.txt");
        assertThat(url0.getProtocol()).isEqualTo("ftp");
        assertThat(url0.getAuthority()).isEqualTo("localhost");
        assertThat(url0.getHost()).isEqualTo("localhost");
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getDefaultPort()).isEqualTo(21);
        assertThat(url0.getFile()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getPath()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRef()).isEqualTo(null);
        assertThat(url0.toExternalForm()).isEqualTo("ftp://localhost/aaa/bbb/ccc.txt");

        url0 = new URL("file:///aaa/bbb/ccc.txt");
        assertThat(url0.getProtocol()).isEqualTo("file");
        assertThat(url0.getAuthority()).isEqualTo("");
        assertThat(url0.getHost()).isEqualTo("");
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getDefaultPort()).isEqualTo(-1);
        assertThat(url0.getFile()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getPath()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRef()).isEqualTo(null);
        assertThat(url0.toExternalForm()).isEqualTo("file:/aaa/bbb/ccc.txt");
        assertThat(url0.toString()).isEqualTo("file:/aaa/bbb/ccc.txt");

        url0 = new URL("file:/aaa/bbb/ccc.txt");
        assertThat(url0.getProtocol()).isEqualTo("file");
        assertThat(url0.getAuthority()).isEqualTo(null); // NOTE : compare to "file:///" constructor.
        assertThat(url0.getHost()).isEqualTo("");
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getDefaultPort()).isEqualTo(-1);
        assertThat(url0.getFile()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getPath()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRef()).isEqualTo(null);
        assertThat(url0.toExternalForm()).isEqualTo("file:/aaa/bbb/ccc.txt");
        assertThat(url0.toString()).isEqualTo("file:/aaa/bbb/ccc.txt");
    }

    static Stream<String> provideMalformedUrls() {
        return Stream.of(
        // @formatter:off
        "",
        "/",
        "/aaa/bbb/ccc.html",
        "C:\\aaa\\bbb\\ccc.html",
        "xxx://localhost/aaa"
        // @formatter:on
        );
    }

    @ParameterizedTest
    @MethodSource("provideMalformedUrls")
    public void testMalformedURLExceptionDemo(String urlstr) {
        assertThatThrownBy(() -> {
            new URL(urlstr);
        }).isInstanceOf(MalformedURLException.class);
    }
}
