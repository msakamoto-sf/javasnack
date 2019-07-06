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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * typical getter usage of {@link java.net.URI}
 */
public class TestURI {

    @Test
    public void testTypicalGetter() throws URISyntaxException {
        URI url0 = new URI("http://localhost/");
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo("http");
        assertThat(url0.getSchemeSpecificPart()).isEqualTo("//localhost/");
        assertThat(url0.getRawSchemeSpecificPart()).isEqualTo("//localhost/");
        assertThat(url0.getAuthority()).isEqualTo("localhost");
        assertThat(url0.getRawAuthority()).isEqualTo("localhost");
        assertThat(url0.getHost()).isEqualTo("localhost");
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo("/");
        assertThat(url0.getRawPath()).isEqualTo("/");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRawQuery()).isEqualTo(null);
        assertThat(url0.getFragment()).isEqualTo(null);
        assertThat(url0.getRawFragment()).isEqualTo(null);
        assertThat(url0.toASCIIString()).isEqualTo("http://localhost/");
        assertThat(url0.toString()).isEqualTo("http://localhost/");

        String url1 = "xxx://username:password@localhost:8080/aaa/bbb;ccc;ddd/eee.txt;fff=ggg?q1=v1&q2=v2#ref123";
        url0 = new URI(url1);
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo("xxx");
        assertThat(url0.getSchemeSpecificPart())
                .isEqualTo("//username:password@localhost:8080/aaa/bbb;ccc;ddd/eee.txt;fff=ggg?q1=v1&q2=v2");
        assertThat(url0.getRawSchemeSpecificPart())
                .isEqualTo("//username:password@localhost:8080/aaa/bbb;ccc;ddd/eee.txt;fff=ggg?q1=v1&q2=v2");
        assertThat(url0.getAuthority()).isEqualTo("username:password@localhost:8080");
        assertThat(url0.getRawAuthority()).isEqualTo("username:password@localhost:8080");
        assertThat(url0.getHost()).isEqualTo("localhost");
        assertThat(url0.getUserInfo()).isEqualTo("username:password");
        assertThat(url0.getRawUserInfo()).isEqualTo("username:password");
        assertThat(url0.getPort()).isEqualTo(8080);
        assertThat(url0.getPath()).isEqualTo("/aaa/bbb;ccc;ddd/eee.txt;fff=ggg");
        assertThat(url0.getRawPath()).isEqualTo("/aaa/bbb;ccc;ddd/eee.txt;fff=ggg");
        assertThat(url0.getQuery()).isEqualTo("q1=v1&q2=v2");
        assertThat(url0.getRawQuery()).isEqualTo("q1=v1&q2=v2");
        assertThat(url0.getFragment()).isEqualTo("ref123");
        assertThat(url0.getRawFragment()).isEqualTo("ref123");
        assertThat(url0.toASCIIString()).isEqualTo(url1);
        assertThat(url0.toString()).isEqualTo(url1);

        String url2 = "xxx://user%20%2Fname:pass%20%2Fword@local%20host:8080/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg?q1=v1&q2%20%2F=v2#ref%2F%20123";
        url0 = new URI(url2);
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo("xxx");
        assertThat(url0.getSchemeSpecificPart())
                .isEqualTo("//user /name:pass /word@local host:8080/aaa/bbb;ccc;ddd /eee.txt;fff=ggg?q1=v1&q2 /=v2");
        assertThat(url0.getRawSchemeSpecificPart())
                .isEqualTo(
                        "//user%20%2Fname:pass%20%2Fword@local%20host:8080/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg?q1=v1&q2%20%2F=v2");
        assertThat(url0.getAuthority()).isEqualTo("user /name:pass /word@local host:8080");
        assertThat(url0.getRawAuthority()).isEqualTo("user%20%2Fname:pass%20%2Fword@local%20host:8080");
        assertThat(url0.getHost()).isEqualTo(null);
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo("/aaa/bbb;ccc;ddd /eee.txt;fff=ggg");
        assertThat(url0.getRawPath()).isEqualTo("/aaa/bbb;ccc;ddd%20%2Feee.txt;fff=ggg");
        assertThat(url0.getQuery()).isEqualTo("q1=v1&q2 /=v2");
        assertThat(url0.getRawQuery()).isEqualTo("q1=v1&q2%20%2F=v2");
        assertThat(url0.getFragment()).isEqualTo("ref/ 123");
        assertThat(url0.getRawFragment()).isEqualTo("ref%2F%20123");
        assertThat(url0.toASCIIString()).isEqualTo(url2);
        assertThat(url0.toString()).isEqualTo(url2);

        url0 = new URI("");
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo(null);
        assertThat(url0.getSchemeSpecificPart()).isEqualTo("");
        assertThat(url0.getRawSchemeSpecificPart()).isEqualTo("");
        assertThat(url0.getAuthority()).isEqualTo(null);
        assertThat(url0.getRawAuthority()).isEqualTo(null);
        assertThat(url0.getHost()).isEqualTo(null);
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo("");
        assertThat(url0.getRawPath()).isEqualTo("");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRawQuery()).isEqualTo(null);
        assertThat(url0.getFragment()).isEqualTo(null);
        assertThat(url0.getRawFragment()).isEqualTo(null);
        assertThat(url0.toASCIIString()).isEqualTo("");
        assertThat(url0.toString()).isEqualTo("");

        url0 = new URI("/");
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo(null);
        assertThat(url0.getSchemeSpecificPart()).isEqualTo("/");
        assertThat(url0.getRawSchemeSpecificPart()).isEqualTo("/");
        assertThat(url0.getAuthority()).isEqualTo(null);
        assertThat(url0.getRawAuthority()).isEqualTo(null);
        assertThat(url0.getHost()).isEqualTo(null);
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo("/");
        assertThat(url0.getRawPath()).isEqualTo("/");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRawQuery()).isEqualTo(null);
        assertThat(url0.getFragment()).isEqualTo(null);
        assertThat(url0.getRawFragment()).isEqualTo(null);
        assertThat(url0.toASCIIString()).isEqualTo("/");
        assertThat(url0.toString()).isEqualTo("/");

        url0 = new URI("a");
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo(null);
        assertThat(url0.getSchemeSpecificPart()).isEqualTo("a");
        assertThat(url0.getRawSchemeSpecificPart()).isEqualTo("a");
        assertThat(url0.getAuthority()).isEqualTo(null);
        assertThat(url0.getRawAuthority()).isEqualTo(null);
        assertThat(url0.getHost()).isEqualTo(null);
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo("a");
        assertThat(url0.getRawPath()).isEqualTo("a");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRawQuery()).isEqualTo(null);
        assertThat(url0.getFragment()).isEqualTo(null);
        assertThat(url0.getRawFragment()).isEqualTo(null);
        assertThat(url0.toASCIIString()).isEqualTo("a");
        assertThat(url0.toString()).isEqualTo("a");

        url0 = new URI("file:///");
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo("file");
        assertThat(url0.getSchemeSpecificPart()).isEqualTo("///");
        assertThat(url0.getRawSchemeSpecificPart()).isEqualTo("///");
        assertThat(url0.getAuthority()).isEqualTo(null);
        assertThat(url0.getRawAuthority()).isEqualTo(null);
        assertThat(url0.getHost()).isEqualTo(null);
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo("/");
        assertThat(url0.getRawPath()).isEqualTo("/");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRawQuery()).isEqualTo(null);
        assertThat(url0.getFragment()).isEqualTo(null);
        assertThat(url0.getRawFragment()).isEqualTo(null);
        assertThat(url0.toASCIIString()).isEqualTo("file:///");
        assertThat(url0.toString()).isEqualTo("file:///");

        url0 = new URI("file:///aaa/bbb/ccc.txt");
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo("file");
        assertThat(url0.getSchemeSpecificPart()).isEqualTo("///aaa/bbb/ccc.txt");
        assertThat(url0.getRawSchemeSpecificPart()).isEqualTo("///aaa/bbb/ccc.txt");
        assertThat(url0.getAuthority()).isEqualTo(null);
        assertThat(url0.getRawAuthority()).isEqualTo(null);
        assertThat(url0.getHost()).isEqualTo(null);
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getRawPath()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRawQuery()).isEqualTo(null);
        assertThat(url0.getFragment()).isEqualTo(null);
        assertThat(url0.getRawFragment()).isEqualTo(null);
        assertThat(url0.toASCIIString()).isEqualTo("file:///aaa/bbb/ccc.txt");
        assertThat(url0.toString()).isEqualTo("file:///aaa/bbb/ccc.txt");

        url0 = new URI("file://C:/aaa/bbb/ccc.txt");
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo("file");
        assertThat(url0.getSchemeSpecificPart()).isEqualTo("//C:/aaa/bbb/ccc.txt");
        assertThat(url0.getRawSchemeSpecificPart()).isEqualTo("//C:/aaa/bbb/ccc.txt");
        assertThat(url0.getAuthority()).isEqualTo("C:");
        assertThat(url0.getRawAuthority()).isEqualTo("C:");
        assertThat(url0.getHost()).isEqualTo("C");
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getRawPath()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRawQuery()).isEqualTo(null);
        assertThat(url0.getFragment()).isEqualTo(null);
        assertThat(url0.getRawFragment()).isEqualTo(null);
        assertThat(url0.toASCIIString()).isEqualTo("file://C:/aaa/bbb/ccc.txt");
        assertThat(url0.toString()).isEqualTo("file://C:/aaa/bbb/ccc.txt");

        url0 = new URI("xxx:yyy");
        assertThat(url0.isOpaque()).isEqualTo(true);
        assertThat(url0.getScheme()).isEqualTo("xxx");
        assertThat(url0.getSchemeSpecificPart()).isEqualTo("yyy");
        assertThat(url0.getRawSchemeSpecificPart()).isEqualTo("yyy");
        assertThat(url0.getAuthority()).isEqualTo(null);
        assertThat(url0.getRawAuthority()).isEqualTo(null);
        assertThat(url0.getHost()).isEqualTo(null);
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo(null);
        assertThat(url0.getRawPath()).isEqualTo(null);
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRawQuery()).isEqualTo(null);
        assertThat(url0.getFragment()).isEqualTo(null);
        assertThat(url0.getRawFragment()).isEqualTo(null);
        assertThat(url0.toASCIIString()).isEqualTo("xxx:yyy");
        assertThat(url0.toString()).isEqualTo("xxx:yyy");

        url0 = new URI("ftp://localhost/aaa/bbb/ccc.txt");
        assertThat(url0.isOpaque()).isEqualTo(false);
        assertThat(url0.getScheme()).isEqualTo("ftp");
        assertThat(url0.getSchemeSpecificPart()).isEqualTo("//localhost/aaa/bbb/ccc.txt");
        assertThat(url0.getRawSchemeSpecificPart()).isEqualTo("//localhost/aaa/bbb/ccc.txt");
        assertThat(url0.getAuthority()).isEqualTo("localhost");
        assertThat(url0.getRawAuthority()).isEqualTo("localhost");
        assertThat(url0.getHost()).isEqualTo("localhost");
        assertThat(url0.getUserInfo()).isEqualTo(null);
        assertThat(url0.getRawUserInfo()).isEqualTo(null);
        assertThat(url0.getPort()).isEqualTo(-1);
        assertThat(url0.getPath()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getRawPath()).isEqualTo("/aaa/bbb/ccc.txt");
        assertThat(url0.getQuery()).isEqualTo(null);
        assertThat(url0.getRawQuery()).isEqualTo(null);
        assertThat(url0.getFragment()).isEqualTo(null);
        assertThat(url0.getRawFragment()).isEqualTo(null);
        assertThat(url0.toASCIIString()).isEqualTo("ftp://localhost/aaa/bbb/ccc.txt");
        assertThat(url0.toString()).isEqualTo("ftp://localhost/aaa/bbb/ccc.txt");
    }

    static Stream<String> provideURISyntaxErrors() {
        return Stream.of(
        // @formatter:off
        "C:\\aaa\\bbb\\ccc.html"
        // @formatter:on
        );
    }

    @ParameterizedTest
    @MethodSource("provideURISyntaxErrors")
    public void testURISyntaxExceptionDemo(String uristr) {
        assertThatThrownBy(() -> {
            new URI(uristr);
        }).isInstanceOf(URISyntaxException.class);
    }

}
