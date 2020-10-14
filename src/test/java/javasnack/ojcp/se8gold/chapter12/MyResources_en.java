package javasnack.ojcp.se8gold.chapter12;

import java.util.ListResourceBundle;

public class MyResources_en extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        Object[][] contents = { { "send", "send" },
                { "cancel", "cancel" } };
        return contents;
    }
}
