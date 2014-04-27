package javasnack.snacks.mustache;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javasnack.snacks.json.pojo.EncodePojo;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

/**
 * mustache.java exercises
 * 
 * @see https://github.com/spullara/mustache.java
 * @see http://d.hatena.ne.jp/Kazuhira/20121209/1355042467
 * @see http://d.hatena.ne.jp/Kazuhira/20121209/1355043946
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class MustacheExercise implements Runnable {

    static class SubScope {
        public String original;
        public String uppercase;

        SubScope(String content) {
            this.original = content;
            this.uppercase = content.toUpperCase();
        }

        public String getOriginal() {
            return original;
        }
    }

    /**
     * <pre>
     * booleanValueTrue:true
     * booleanValueFalse:false
     * charValue:￿
     * byteValue:127
     * shortValue:32767
     * intValue:2147483647
     * longValue:9223372036854775807
     * floatValue:3.4028235E38
     * doubleValue:1.7976931348623157E308
     * fileObject:DUMMY
     * charsetObject:UTF-8
     * patternObject:.*
     * urlObject:http://www.example.com
     * uriObject:dummy://dummy.dummy/dummy
     * inetAddressObject:/192.168.1.1
     * tzObject:sun.util.calendar.ZoneInfo[id=&quot;GMT&quot;,offset=0,dstSavings=0,useDaylight=false,transitions=0,lastRule=null]
     * calendarObject:java.util.GregorianCalendar[time=46923456,areFieldsSet=true,areAllFieldsSet=false,lenient=true,zone=sun.util.calendar.ZoneInfo[id=&quot;GMT&quot;,offset=0,dstSavings=0,useDaylight=false,transitions=0,lastRule=null],firstDayOfWeek=1,minimalDaysInFirstWeek=1,ERA=1,YEAR=1970,MONTH=0,WEEK_OF_YEAR=1,WEEK_OF_MONTH=1,DAY_OF_MONTH=1,DAY_OF_YEAR=1,DAY_OF_WEEK=5,DAY_OF_WEEK_IN_MONTH=1,AM_PM=1,HOUR=1,HOUR_OF_DAY=13,MINUTE=2,SECOND=3,MILLISECOND=456,ZONE_OFFSET=0,DST_OFFSET=0]
     * dateObject:Thu Jan 01 22:02:03 JST 1970
     * sqlTimestampObject:1970-01-01 22:02:03.456
     * nullObject:
     * booleanArray:true,false,
     * charArray:a,b,c,
     * byteArray:127,0,-128,
     * shortArray:32767,0,-32768,
     * intArray:2147483647,0,-2147483648,
     * longArray:9223372036854775807,0,-9223372036854775808,
     * floatArray:3.4028235E38,0.0,1.4E-45,
     * doubleArray:1.7976931348623157E308,0.0,4.9E-324,
     * listObject:DUMMY,UTF-8,.*,[C@102a648c,[B@4f2f2b9e,[S@21d8baaf,
     * mapObject:str:HelloJson
     * file:DUMMY
     * charset:UTF-8
     * null:
     * byte_array:127,0,-128,
     * char_array:a,b,c,
     * int_array:2147483647,0,-2147483648,
     * list:DUMMY,UTF-8,.*,[C@102a648c,[B@4f2f2b9e,[S@21d8baaf,
     * 
     * 日本語プロパティ:日本語文字列
     * enumObject:ONE
     * enumArray:TWO,FOUR,
     * enumList:THREE,FIVE,
     * subScope:original:HelloMustache
     * uppercase:HELLOMUSTACHE
     * 
     * subScopes:original:subscope1
     * uppercase:SUBSCOPE1
     * original:subscope2
     * uppercase:SUBSCOPE2
     * </pre>
     * 
     * @throws Exception
     */
    void pojoscope() throws Exception {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("mustache-java-tmpl/pojo.mustache");
        EncodePojo boundScope = new EncodePojo() {
            SubScope subScope = new SubScope("HelloMustache");
            List<SubScope> subScopes = Arrays.asList(new SubScope("subscope1"),
                    new SubScope("subscope2"));
        };
        mustache.execute(new PrintWriter(System.out), boundScope).flush();
    }

    /**
     * <pre>
     * -------------------
     * {k1=100, k2=abc}
     * -------------------
     * </pre>
     * 
     * @throws Exception
     */
    void mapmiss() throws Exception {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile("mustache-java-tmpl/mapmiss.mustache");
        Map<String, Object> m1 = new HashMap<>();
        m1.put("k1", new Integer(100));
        m1.put("k2", "abc");
        Map<String, Object> boundScope = new HashMap<>();
        boundScope.put("m1", m1);
        mustache.execute(new PrintWriter(System.out), boundScope).flush();
    }

    /**
     * <pre>
     * item1=hello
     * empty(item2)
     * empty(item3)
     * empty(item4)
     * empty(item5)
     * </pre>
     * 
     * NOTE : cond3 and item3 behaviour is differ to Hogan.groovy conditions.
     * 
     * @throws Exception
     */
    void conditions() throws Exception {
        Map<String, Object> boundScope = new HashMap<>();
        boundScope.put("cond1", true);
        boundScope.put("item1", "hello");
        boundScope.put("cond2", false);
        boundScope.put("item2", "bonjour");
        boundScope.put("cond3", "");
        boundScope.put("item3", "morning");
        boundScope.put("cond4", Collections.EMPTY_LIST);
        boundScope.put("item4", "afternoon");
        boundScope.put("cond5", null);
        boundScope.put("item5", "evening");

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf
                .compile("mustache-java-tmpl/conditions.mustache");
        mustache.execute(new PrintWriter(System.out), boundScope).flush();
    }

/**
     * <pre>
     * escaped : &lt;&quot;\&#39;&amp;&gt;
     * unescaped : <"\'&>
     * </pre>
     * 
     * @throws Exception
     */
    void htmlescape() throws Exception {
        Object boundScope = new Object() {
            public String val = "<\"\\'&>";
        };
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf
                .compile("mustache-java-tmpl/htmlescape.mustache");
        mustache.execute(new PrintWriter(System.out), boundScope).flush();
    }

    @Override
    public void run() {
        try {
            System.out.println("================= pojoscope");
            pojoscope();
            System.out.println("================= mapmiss");
            mapmiss();
            System.out.println("================= conditions");
            conditions();
            System.out.println("================= htmlescape");
            htmlescape();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
