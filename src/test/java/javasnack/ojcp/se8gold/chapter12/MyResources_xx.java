package javasnack.ojcp.se8gold.chapter12;

import java.util.ListResourceBundle;

public class MyResources_xx extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        Object[][] contents = { { "send", "sendx" },
                { "cancel", "cancelx" } };
        return contents;
    }
}
