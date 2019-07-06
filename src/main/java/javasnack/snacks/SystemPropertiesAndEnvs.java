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

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class SystemPropertiesAndEnvs implements Runnable {

    @Override
    public void run() {
        System.out.println("System.getenv():");
        Map<String, String> envs = System.getenv();
        for (Map.Entry<String, String> env : envs.entrySet()) {
            System.out.println(String.format("[%s]=[%s]", env.getKey(),
                    env.getValue()));
        }
        System.out.println("-------------------------");
        System.out.println("System.getProperties():");
        Properties prop = System.getProperties();
        Set<Object> propKeys = new TreeSet<Object>(prop.keySet());
        for (Object key : propKeys) {
            String k = key.toString();
            String v = prop.getProperty(k);
            System.out.println(String.format("[%s]=[%s]", k, v));
        }
    }
}
