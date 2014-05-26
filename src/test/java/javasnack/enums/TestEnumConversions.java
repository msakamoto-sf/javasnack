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

import static org.testng.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

public class TestEnumConversions {

    /*
     * Simple Enum Conversions
     */

    enum NumericBasicEnums {
        ONE, TWO, THREE, FOUR, FIVE,
    }

    @Test
    public void numericBasicEnumToOrdinalInt() {
        assertEquals(NumericBasicEnums.ONE.ordinal(), 0);
        assertEquals(NumericBasicEnums.TWO.ordinal(), 1);
        assertEquals(NumericBasicEnums.THREE.ordinal(), 2);
        assertEquals(NumericBasicEnums.FOUR.ordinal(), 3);
        assertEquals(NumericBasicEnums.FIVE.ordinal(), 4);
    }

    @Test
    public void numericBasicEnumToString() {
        assertEquals(NumericBasicEnums.ONE.name(), "ONE");
        assertEquals(NumericBasicEnums.TWO.name(), "TWO");
        assertEquals(NumericBasicEnums.THREE.name(), "THREE");
        assertEquals(NumericBasicEnums.FOUR.name(), "FOUR");
        assertEquals(NumericBasicEnums.FIVE.name(), "FIVE");

        assertEquals(NumericBasicEnums.ONE.toString(), "ONE");
        assertEquals(NumericBasicEnums.TWO.toString(), "TWO");
        assertEquals(NumericBasicEnums.THREE.toString(), "THREE");
        assertEquals(NumericBasicEnums.FOUR.toString(), "FOUR");
        assertEquals(NumericBasicEnums.FIVE.toString(), "FIVE");
    }

    @Test
    public void stringToNumericBasicEnum() {
        assertEquals(NumericBasicEnums.valueOf("ONE"), NumericBasicEnums.ONE);
        assertEquals(NumericBasicEnums.valueOf("TWO"), NumericBasicEnums.TWO);
    }

    @Test
    public void ordinalToNumericBasicEnum() {
        NumericBasicEnums[] ens = NumericBasicEnums.values();
        assertEquals(ens.length, 5);
        assertEquals(ens[0], NumericBasicEnums.ONE);
        assertEquals(ens[1], NumericBasicEnums.TWO);
        assertEquals(ens[2], NumericBasicEnums.THREE);
        assertEquals(ens[3], NumericBasicEnums.FOUR);
        assertEquals(ens[4], NumericBasicEnums.FIVE);
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
        assertEquals(NumericConstEnums.UNDEFINED.ordinal(), 0);
        assertEquals(NumericConstEnums.ONE.ordinal(), 1);
        assertEquals(NumericConstEnums.TWO.ordinal(), 2);
        assertEquals(NumericConstEnums.THREE.ordinal(), 3);
    }

    @Test
    public void numericConstEnumToString() {
        assertEquals(NumericConstEnums.UNDEFINED.name(), "UNDEFINED");
        assertEquals(NumericConstEnums.ONE.name(), "ONE");
        assertEquals(NumericConstEnums.TWO.name(), "TWO");
        assertEquals(NumericConstEnums.THREE.name(), "THREE");

        assertEquals(NumericConstEnums.UNDEFINED.toString(), "UNDEFINED");
        assertEquals(NumericConstEnums.ONE.toString(), "ONE");
        assertEquals(NumericConstEnums.TWO.toString(), "TWO");
        assertEquals(NumericConstEnums.THREE.toString(), "THREE");
    }

    @Test
    public void stringToNumericConstEnum() {
        assertEquals(NumericConstEnums.valueOf("ONE"), NumericConstEnums.ONE);
        assertEquals(NumericConstEnums.valueOf("TWO"), NumericConstEnums.TWO);
    }

    @Test
    public void ordinalToNumericConstEnum() {
        NumericConstEnums[] ens = NumericConstEnums.values();
        assertEquals(ens.length, 4);
        assertEquals(ens[0], NumericConstEnums.UNDEFINED);
        assertEquals(ens[1], NumericConstEnums.ONE);
        assertEquals(ens[2], NumericConstEnums.TWO);
        assertEquals(ens[3], NumericConstEnums.THREE);
    }

    @Test
    public void numericConstConversionDemos() {
        NumericConstEnums[] samples = NumericConstEnums.values();
        for (NumericConstEnums e : samples) {
            assertEquals(NumericConstEnums.from(e.v()), e);
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
        assertEquals(StringConstEnums.ENABLED.ordinal(), 0);
        assertEquals(StringConstEnums.DISABLED.ordinal(), 1);
    }

    @Test
    public void stringConstEnumToString() {
        assertEquals(StringConstEnums.ENABLED.name(), "ENABLED");
        assertEquals(StringConstEnums.DISABLED.name(), "DISABLED");

        assertEquals(StringConstEnums.ENABLED.toString(), "ENABLED");
        assertEquals(StringConstEnums.DISABLED.toString(), "DISABLED");
    }

    @Test
    public void stringToStringConstEnum() {
        assertEquals(StringConstEnums.valueOf("ENABLED"),
                StringConstEnums.ENABLED);
        assertEquals(StringConstEnums.valueOf("DISABLED"),
                StringConstEnums.DISABLED);
    }

    @Test
    public void ordinalToStringConstEnum() {
        StringConstEnums[] ens = StringConstEnums.values();
        assertEquals(ens.length, 2);
        assertEquals(ens[0], StringConstEnums.ENABLED);
        assertEquals(ens[1], StringConstEnums.DISABLED);
    }

    @Test
    public void stringConstConversionDemos() {
        StringConstEnums[] samples = StringConstEnums.values();
        for (StringConstEnums e : samples) {
            assertEquals(StringConstEnums.from(e.v()), e);
        }
    }

}
