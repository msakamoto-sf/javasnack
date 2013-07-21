package javasnack.snacks;

import java.io.InputStream;

import myapi.GreetingInterface;

import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.JclUtils;

/**
 * Tiny Demonstration for https://github.com/kamranzafar/JCL
 * 
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class JCLDemo implements Runnable {

    void sandbox(String jarResourceName) {
        try {
            InputStream jar = this.getClass().getClassLoader()
                    .getResourceAsStream(jarResourceName);
            JarClassLoader jcl = new JarClassLoader();
            jcl.add(jar);
            JclObjectFactory factory = JclObjectFactory.getInstance();

            // call constructor with arguments.
            Object obj = factory.create(jcl, "myapi.GreetingImpl",
                    new String[] { "Firstname", "Lastname" }, new Class[] {
                            String.class, String.class });

            GreetingInterface gi = JclUtils.cast(obj, GreetingInterface.class);
            System.out.println(gi.morning("Darjeeling"));
            System.out.println(gi.afternoon("Assam"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("===== load myapi-impl1.jar");
        sandbox("JCLDemo/myapi-impl1.jar");
        System.out.println("===== load myapi-impl2.jar");
        sandbox("JCLDemo/myapi-impl2.jar");
        System.out.println("(END)");
    }

}
