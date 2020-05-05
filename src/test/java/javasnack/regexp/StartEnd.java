/*
 * Copyright 2020 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.regexp;

import java.util.regex.Matcher;

import lombok.Value;

@Value(staticConstructor = "of")
public class StartEnd {
    public final int start;
    public final int end;
    public final int groupIndex;
    public final String groupText;

    static StartEnd se(final int start, final int end, final int groupIndex, final String groupText) {
        return StartEnd.of(start, end, groupIndex, groupText);
    }

    static StartEnd se(final int start, final int end) {
        return StartEnd.of(start, end, -1, "");
    }

    static StartEnd se(final Matcher m) {
        return StartEnd.of(m.start(), m.end(), -1, "");
    }

    static StartEnd se(final Matcher m, final int groupIndex) {
        return StartEnd.of(m.start(groupIndex), m.end(groupIndex), groupIndex, m.group(groupIndex));
    }
}
