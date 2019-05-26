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
package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @see http://docs.oracle.com/javase/8/docs/technotes/guides/language/strings-switch.html
 */
public class TestStringSwitch {

    public int switchDemo(String s) {
        switch (s.toLowerCase()) {
        case "one":
            return 1;
        case "two":
            return 2;
        case "three":
            return 3;
        default:
            return 0;
        }
    }

    @Test
    public void demo() {
        assertThat(switchDemo("one")).isEqualTo(1);
        assertThat(switchDemo("Two")).isEqualTo(2);
        assertThat(switchDemo("THREE")).isEqualTo(3);
        assertThat(switchDemo("four")).isEqualTo(0);
    }
}
