package javasnack.ojcp.se8gold.chapter12;

import java.util.ListResourceBundle;

public class MyResources2 extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        final Integer int1 = 500;
        final Long long1 = 10_000L;
        final int[] ints = { 10, 20, 30 };
        final String[] strings = { "aa", "bb", "cc" };
        Object[][] contents = {
            // @formatter:off
            { "int1", int1 }
           ,{ "long1", long1 }
           ,{ "ints", ints }
           ,{ "str1", "abcd" }
           ,{ "strings1", "AA", "BB", "CC" }
           ,{ "strings2", strings }
            // @formatter:on
        };
        return contents;
    }
}
