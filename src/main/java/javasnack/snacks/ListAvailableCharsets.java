package javasnack.snacks;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

public class ListAvailableCharsets implements Runnable {

    @Override
    public void run() {
        Charset csDefault = Charset.defaultCharset();
        String csDefaultCanonicalName = csDefault.name();
        Map<String, Charset> acs = Charset.availableCharsets();
        Iterator<String> acNamesIt = acs.keySet().iterator();
        while (acNamesIt.hasNext()) {
            String canonicalName = acNamesIt.next();
            Charset cs = acs.get(canonicalName);
            String displayName = cs.displayName();
            String h = "[" + canonicalName;
            if (canonicalName.equals(csDefaultCanonicalName)) {
                h += " (*DefaultCharset*)";
            }
            h += "](displayName=" + displayName + ")";
            System.out.println(h);
            Iterator<String> aliasesIt = cs.aliases().iterator();
            while (aliasesIt.hasNext()) {
                System.out.println("alias:[" + aliasesIt.next() + "]");
            }
        }
    }

}
