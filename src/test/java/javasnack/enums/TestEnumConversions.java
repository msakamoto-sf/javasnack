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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class TestEnumConversions {

    /*
     * Simple Enum Conversions
     */

    enum NumericBasicEnums {
        ONE, TWO, THREE, FOUR, FIVE,
    }

    @Test
    public void numericBasicEnumToOrdinalInt() {
        assertEquals(0, NumericBasicEnums.ONE.ordinal());
        assertEquals(1, NumericBasicEnums.TWO.ordinal());
        assertEquals(2, NumericBasicEnums.THREE.ordinal());
        assertEquals(3, NumericBasicEnums.FOUR.ordinal());
        assertEquals(4, NumericBasicEnums.FIVE.ordinal());
    }

    @Test
    public void numericBasicEnumToString() {
        assertEquals("ONE", NumericBasicEnums.ONE.name());
        assertEquals("TWO", NumericBasicEnums.TWO.name());
        assertEquals("THREE", NumericBasicEnums.THREE.name());
        assertEquals("FOUR", NumericBasicEnums.FOUR.name());
        assertEquals("FIVE", NumericBasicEnums.FIVE.name());

        assertEquals("ONE", NumericBasicEnums.ONE.toString());
        assertEquals("TWO", NumericBasicEnums.TWO.toString());
        assertEquals("THREE", NumericBasicEnums.THREE.toString());
        assertEquals("FOUR", NumericBasicEnums.FOUR.toString());
        assertEquals("FIVE", NumericBasicEnums.FIVE.toString());
    }

    @Test
    public void stringToNumericBasicEnum() {
        assertEquals(NumericBasicEnums.ONE, NumericBasicEnums.valueOf("ONE"));
        assertEquals(NumericBasicEnums.TWO, NumericBasicEnums.valueOf("TWO"));
    }

    @Test
    public void ordinalToNumericBasicEnum() {
        NumericBasicEnums[] ens = NumericBasicEnums.values();
        assertEquals(5, ens.length);
        assertEquals(NumericBasicEnums.ONE, ens[0]);
        assertEquals(NumericBasicEnums.TWO, ens[1]);
        assertEquals(NumericBasicEnums.THREE, ens[2]);
        assertEquals(NumericBasicEnums.FOUR, ens[3]);
        assertEquals(NumericBasicEnums.FIVE, ens[4]);
    }

    /*
     * Numeric "CONST" Enum Conversions
     */

    enum NumericConstEnums {
        UNDEFINED(-1), ONE(1), TWO(2), THREE(3);
        final int n;

        NumericConstEnums(int n) {
            this.n = n;
        }

        int v() {
            return this.n;
        }

        /**
         * @see http://javatechnology.net/java/int-enum/
         * @see http://stackoverflow.com/questions/5878952/cast-int-to-enum-in-java
         * @see http://stackoverflow.com/questions/5292790/convert-integer-value-to-matching-java-enum
         * @see http://stackoverflow.com/questions/7996335/how-to-match-int-to-enum
         * @param t
         * @return
         */
        static NumericConstEnums from(int t) {
            // maybe faster than HashMap<Integer, NumericConstEnums> solution :p
            NumericConstEnums[] ens = NumericConstEnums.values();
            for (NumericConstEnums e : ens) {
                if (e.n == t) {
                    return e;
                }
            }
            // you can throw IllegalArgumentException here. maybe good interface.
            return UNDEFINED;
        }
    }

    @Test
    public void numericConstEnumToOrdinalInt() {
        assertEquals(0, NumericConstEnums.UNDEFINED.ordinal());
        assertEquals(1, NumericConstEnums.ONE.ordinal());
        assertEquals(2, NumericConstEnums.TWO.ordinal());
        assertEquals(3, NumericConstEnums.THREE.ordinal());
    }

    @Test
    public void numericConstEnumToString() {
        assertEquals("UNDEFINED", NumericConstEnums.UNDEFINED.name());
        assertEquals("ONE", NumericConstEnums.ONE.name());
        assertEquals("TWO", NumericConstEnums.TWO.name());
        assertEquals("THREE", NumericConstEnums.THREE.name());

        assertEquals("UNDEFINED", NumericConstEnums.UNDEFINED.toString());
        assertEquals("ONE", NumericConstEnums.ONE.toString());
        assertEquals("TWO", NumericConstEnums.TWO.toString());
        assertEquals("THREE", NumericConstEnums.THREE.toString());
    }

    @Test
    public void stringToNumericConstEnum() {
        assertEquals(NumericConstEnums.ONE, NumericConstEnums.valueOf("ONE"));
        assertEquals(NumericConstEnums.TWO, NumericConstEnums.valueOf("TWO"));
    }

    @Test
    public void ordinalToNumericConstEnum() {
        NumericConstEnums[] ens = NumericConstEnums.values();
        assertEquals(4, ens.length);
        assertEquals(NumericConstEnums.UNDEFINED, ens[0]);
        assertEquals(NumericConstEnums.ONE, ens[1]);
        assertEquals(NumericConstEnums.TWO, ens[2]);
        assertEquals(NumericConstEnums.THREE, ens[3]);
    }

    @Test
    public void numericConstConversionDemos() {
        NumericConstEnums[] samples = NumericConstEnums.values();
        for (NumericConstEnums e : samples) {
            assertEquals(e, NumericConstEnums.from(e.v()));
        }
    }

    /*
     * String "CONST" Enum Conversions
     */

    enum StringConstEnums {
        ENABLED("true"), DISABLED("false");

        final String n;

        StringConstEnums(String n) {
            this.n = n;
        }

        String v() {
            return this.n;
        }

        // from Effective Java (2nd) (with some customized)

        static final Map<String, StringConstEnums> stringToEnum = new HashMap<>();

        static {
            for (StringConstEnums e : StringConstEnums.values()) {
                stringToEnum.put(e.v(), e);
            }
        }

        public static StringConstEnums from(String v) {
            if (stringToEnum.containsKey(v)) {
                return stringToEnum.get(v);
            }
            throw new IllegalArgumentException("unmatch value: " + v);
        }
    }

    @Test
    public void stringConstEnumToOrdinalInt() {
        assertEquals(0, StringConstEnums.ENABLED.ordinal());
        assertEquals(1, StringConstEnums.DISABLED.ordinal());
    }

    @Test
    public void stringConstEnumToString() {
        assertEquals("ENABLED", StringConstEnums.ENABLED.name());
        assertEquals("DISABLED", StringConstEnums.DISABLED.name());

        assertEquals("ENABLED", StringConstEnums.ENABLED.toString());
        assertEquals("DISABLED", StringConstEnums.DISABLED.toString());
    }

    @Test
    public void stringToStringConstEnum() {
        assertEquals(StringConstEnums.ENABLED, StringConstEnums.valueOf("ENABLED"));
        assertEquals(StringConstEnums.DISABLED, StringConstEnums.valueOf("DISABLED"));
    }

    @Test
    public void ordinalToStringConstEnum() {
        StringConstEnums[] ens = StringConstEnums.values();
        assertEquals(2, ens.length);
        assertEquals(StringConstEnums.ENABLED, ens[0]);
        assertEquals(StringConstEnums.DISABLED, ens[1]);
    }

    @Test
    public void stringConstConversionDemos() {
        StringConstEnums[] samples = StringConstEnums.values();
        for (StringConstEnums e : samples) {
            assertEquals(e, StringConstEnums.from(e.v()));
        }
    }

}
