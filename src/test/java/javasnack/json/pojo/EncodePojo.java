/*
 * Copyright 2014 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.json.pojo;

import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class EncodePojo {
    public final boolean booleanValueTrue = true;
    public final boolean booleanValueFalse = false;
    public final char charValue = Character.MAX_VALUE;
    public final byte byteValue = Byte.MAX_VALUE;
    public final short shortValue = Short.MAX_VALUE;
    public final int intValue = Integer.MAX_VALUE;
    public final long longValue = Long.MAX_VALUE;
    public final float floatValue = Float.MAX_VALUE;
    public final double doubleValue = Double.MAX_VALUE;
    public final Charset charsetObject = Charset.forName("UTF-8");
    public final Pattern patternObject = Pattern.compile(".*");
    public final URL urlObject;
    public final URI uriObject = new URI("dummy://dummy.dummy/dummy");
    // 意図的にIPアドレスをハードコードしているため、PMD:AvoidUsingHardCodeIP を行単位で無効化
    // see: https://pmd.github.io/latest/pmd_userdocs_suppressing_warnings.html#nopmd-comment
    public final InetAddress inetAddressObject = InetAddress.getByName("192.168.1.1"); // NOPMD
    public final TimeZone tzObject = TimeZone.getTimeZone("GMT");
    public final Calendar calendarObject;
    public final Date dateObject;
    public final Timestamp sqlTimestampObject;
    public Object nullObject = null;
    public final boolean[] booleanArray = { true, false };
    public final char[] charArray = { 'a', 'b', 'c' };
    public final byte[] byteArray = { Byte.MAX_VALUE, 0, Byte.MIN_VALUE };
    public final short[] shortArray = { Short.MAX_VALUE, 0, Short.MIN_VALUE };
    public final int[] intArray = { Integer.MAX_VALUE, 0, Integer.MIN_VALUE };
    public final long[] longArray = { Long.MAX_VALUE, 0L, Long.MIN_VALUE };
    public final float[] floatArray = { Float.MAX_VALUE, 0.0F, Float.MIN_VALUE };
    public final double[] doubleArray = { Double.MAX_VALUE, 0, Double.MIN_VALUE };
    public final List<Object> listObject = Arrays.asList(
            charsetObject, patternObject, charArray, byteArray, shortArray);
    public final Map<String, Object> mapObject = new LinkedHashMap<>();
    public final String 日本語プロパティ = "日本語文字列";
    public final EncodePojoEnum enumObject = EncodePojoEnum.ONE;
    public final EncodePojoEnum[] enumArray = { EncodePojoEnum.TWO, EncodePojoEnum.FOUR };
    public final List<EncodePojoEnum> enumList = Arrays.asList(
            EncodePojoEnum.THREE, EncodePojoEnum.FIVE);

    public EncodePojo() throws Exception {
        this.urlObject = new URL("http://www.example.com");
        this.calendarObject = Calendar.getInstance(tzObject);
        calendarObject.set(Calendar.YEAR, 1970);
        calendarObject.set(Calendar.MONTH, 0);
        calendarObject.set(Calendar.DAY_OF_MONTH, 1);
        calendarObject.set(Calendar.HOUR_OF_DAY, 1);
        calendarObject.set(Calendar.MINUTE, 2);
        calendarObject.set(Calendar.SECOND, 3);
        calendarObject.set(Calendar.MILLISECOND, 456);
        this.dateObject = new Date(calendarObject.getTimeInMillis());
        this.sqlTimestampObject = new Timestamp(
                calendarObject.getTimeInMillis());
        this.mapObject.put("str", "HelloJson");
        this.mapObject.put("charset", charsetObject);
        this.mapObject.put("null", null);
        this.mapObject.put("byte_array", byteArray);
        this.mapObject.put("char_array", charArray);
        this.mapObject.put("int_array", intArray);
        this.mapObject.put("list", listObject);
    }

    public EncodePojoChild getChild() {
        return new EncodePojoChild(10);
    }

    public List<EncodePojoChild> getChildren() {
        return Arrays.asList(new EncodePojoChild(20), new EncodePojoChild(30));
    }

    public String get日本語ゲッター() {
        return "日本語ゲッターの値";
    }
}
