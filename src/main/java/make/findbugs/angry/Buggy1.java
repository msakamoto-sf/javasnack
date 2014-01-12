package make.findbugs.angry;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Buggy1 {

    int div;

    String s1;

    volatile String[] nonsenseVolatileArray;

    volatile List<String> nonsenseVolatileReference;

    public Buggy1(int a, int b) {
        this.div = a / b;
        this.nonsenseVolatileReference = new ArrayList<String>();
    }

    public void unclosedStream(String fname) {
        try {
            InputStream i = new FileInputStream(fname);
            i.read();
        } catch (Exception e) {
        }
    }

    public String buggySwitch(int i) {
        String s = "";
        switch (i) {
        case 100:
            s = "100";
        case 200:
            s = "200";
        default:
            s = "0";
        }
        return s;
    }
}
