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
