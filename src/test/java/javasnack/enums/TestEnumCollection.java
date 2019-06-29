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
package javasnack.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * @author "Masahiko Sakamoto"(msakamoto-sf, sakamoto.gsyc.3s@gmail.com)
 */
/* EnumMap/EnumSet demos by test cases.
 * see:
 * http://www.atmarkit.co.jp/ait/articles/1103/03/news107.html
 */
public class TestEnumCollection {

    enum NumericEnums {
        ONE, TWO, THREE, FOUR, FIVE,
    }

    @Test
    public void enumMap() {
        Map<NumericEnums, String> numerics = new EnumMap<>(NumericEnums.class);
        numerics.put(NumericEnums.ONE, "one");
        numerics.put(NumericEnums.TWO, "two");
        numerics.put(NumericEnums.THREE, "three");
        numerics.put(NumericEnums.FOUR, "four");
        numerics.put(NumericEnums.FIVE, "five");

        assertEquals("four", numerics.get(NumericEnums.FOUR));

    }

    @Test
    public void enumSet() {
        Set<NumericEnums> s1 = EnumSet.of(NumericEnums.TWO, NumericEnums.THREE);
        assertTrue(s1.contains(NumericEnums.TWO));
        assertFalse(s1.contains(NumericEnums.FOUR));

        Set<NumericEnums> s2 = EnumSet.noneOf(NumericEnums.class);
        assertEquals(0, s2.size());

        Set<NumericEnums> s3 = EnumSet.range(NumericEnums.TWO,
                NumericEnums.FOUR);
        assertFalse(s3.contains(NumericEnums.ONE));
        assertTrue(s3.contains(NumericEnums.TWO));
        assertTrue(s3.contains(NumericEnums.THREE));
        assertTrue(s3.contains(NumericEnums.FOUR));
        assertFalse(s3.contains(NumericEnums.FIVE));
    }

}
