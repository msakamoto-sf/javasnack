package javasnack.ojcp.se8gold.chapter12;

import java.util.ListResourceBundle;

public class MyResources_ja extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        Object[][] contents = { { "send", "送信" },
                { "cancel", "取消" } };
        return contents;
    }
}
