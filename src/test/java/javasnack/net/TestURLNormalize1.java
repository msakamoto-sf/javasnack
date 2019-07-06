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
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Convert relative URL to absolute URL by URL constructor.
 */
/* see:
 * https://www.ietf.org/rfc/rfc2396.txt
 * https://www.ietf.org/rfc/rfc7230.txt
 */
public class TestURLNormalize1 {

    static Stream<Arguments> provideNomalizeUrlSamples() {
        return Stream.of(
                arguments("http://www.example.com/", "", "http://www.example.com/"),
                arguments("http://www.example.com/", "../../../../../../", "http://www.example.com/../../"),
                arguments("http://www.example.com/a/b/c/", "/d/e/f.html?p=%20%0D%0A",
                        "http://www.example.com/d/e/f.html?p=%20%0D%0A"),
                arguments("http://www.example.com/a/b/c/", "../../g/h/./../i/hello world.html?p=%20%0D%0A",
                        "http://www.example.com/a/g/i/hello world.html?p=%20%0D%0A"),
                arguments("http://www.example.com/a/b/c/test.html", "../../g/h/./../i/hello world.html?p=%20%0D%0A",
                        "http://www.example.com/a/g/i/hello world.html?p=%20%0D%0A"),
                arguments("http://www.example.com/hello.html", "/thanks.html;abc=def?p=%20%0D%0A#def",
                        "http://www.example.com/thanks.html;abc=def?p=%20%0D%0A#def"),
                arguments("http://www.example.com/", "/", "http://www.example.com/"));
    }

    @ParameterizedTest
    @MethodSource("provideNomalizeUrlSamples")
    public void testNormalizeSampleUrls(String ctx, String relative, String expected) throws MalformedURLException {
        URL ctxUrl = new URL(ctx);
        URL rUrl = new URL(ctxUrl, relative);
        assertThat(rUrl.toExternalForm()).isEqualTo(expected);
    }
}
