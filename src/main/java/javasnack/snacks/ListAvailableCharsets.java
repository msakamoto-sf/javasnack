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

package javasnack.snacks;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import javasnack.RunnableSnack;

public class ListAvailableCharsets implements RunnableSnack {
    @Override
    public void run(final String... args) {
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
