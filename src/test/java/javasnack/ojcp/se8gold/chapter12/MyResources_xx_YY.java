package javasnack.ojcp.se8gold.chapter12;

import java.util.ListResourceBundle;

public class MyResources_xx_YY extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        Object[][] contents = { { "send", "sendxy" },
                { "cancel", "cancelxy" } };
        return contents;
    }
}
