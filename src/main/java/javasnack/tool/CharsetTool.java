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

import java.nio.charset.Charset;

public interface CharsetTool {
    // nio's Charset canonical name short cuts
    String BINARY = "ISO-8859-1";
    String LATIN1 = "ISO-8859-1";
    String SJIS = "Shift_JIS";
    String MS932 = "windows-31j";
    String EUCJP = "EUC-JP";
    String ISO2022JP = "ISO-2022-JP";
    String UTF8 = "UTF-8";

    // nio's Charset instance short cuts
    Charset CS_BINARY = Charset.forName(BINARY);
    Charset CS_LATIN1 = Charset.forName(LATIN1);
    Charset CS_SJIS = Charset.forName(SJIS);
    Charset CS_MS932 = Charset.forName(MS932);
    Charset CS_EUCJP = Charset.forName(EUCJP);
    Charset CS_ISO2022JP = Charset.forName(ISO2022JP);
    Charset CS_UTF8 = Charset.forName(UTF8);
}
