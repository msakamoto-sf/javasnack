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

import java.io.InputStream;

import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.JclUtils;

import myapi.GreetingImpl;
import myapi.GreetingInterface;

/**
 * Tiny Demonstration for https://github.com/kamranzafar/JCL
 * 
 * @author "Masahiko Sakamoto"(msakamoto-sf, sakamoto.gsyc.3s@gmail.com)
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
                    new String[] { "Firstname", "Lastname" },
                    new Class[] { String.class, String.class });

            GreetingInterface gi = JclUtils.cast(obj, GreetingInterface.class);
            System.out.println(gi.morning("Darjeeling"));
            System.out.println(gi.afternoon("Assam"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("===== Default GreetingImpl:");
        GreetingImpl gi = new GreetingImpl("Firstname", "Lastname");
        System.out.println(gi.morning("Java"));
        System.out.println(gi.afternoon("Java"));

        System.out.println("===== load myapi-impl1.jar");
        sandbox("JCLDemo/myapi-impl1.jar");
        System.out.println("===== load myapi-impl2.jar");
        sandbox("JCLDemo/myapi-impl2.jar");
        System.out.println("(END)");
    }

}
