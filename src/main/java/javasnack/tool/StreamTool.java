/*
 * Copyright 2013 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
package javasnack.tool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StreamTool {

    public static InputStream loadResource(final String path) {
        return StreamTool.class.getClassLoader().getResourceAsStream(path);
    }

    public static byte[] res2ba(final String path) throws IOException {
        return loadResource(path).readAllBytes();
    }

    public static String res2str(final String path) throws IOException {
        return new String(res2ba(path), StandardCharsets.UTF_8);
    }

    public static String res2str(final String path, final Charset cs) throws IOException {
        return new String(res2ba(path), cs);
    }
}
