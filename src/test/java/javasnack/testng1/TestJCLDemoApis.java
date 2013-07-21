/*
 * Copyright 2013 the original author or authors.
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
package javasnack.testng1;

import static org.testng.Assert.*;

import java.io.InputStream;

import myapi.GreetingInterface;

import org.testng.annotations.Test;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;
import org.xeustechnologies.jcl.JclUtils;

/**
 * Tiny Demonstration for https://github.com/kamranzafar/JCL
 * 
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
 */
public class TestJCLDemoApis {

    GreetingInterface loadGreetingInterface(String jarResourceName) {
        InputStream jar = this.getClass().getClassLoader()
                .getResourceAsStream(jarResourceName);
        JarClassLoader jcl = new JarClassLoader();
        jcl.add(jar);
        JclObjectFactory factory = JclObjectFactory.getInstance();

        // call constructor with arguments.
        Object obj = factory.create(jcl, "myapi.GreetingImpl", new String[] {
                "Firstname", "Lastname" }, new Class[] { String.class,
                String.class });

        return JclUtils.cast(obj, GreetingInterface.class);
    }

    @Test
    public void testImpl1() {
        GreetingInterface gi = loadGreetingInterface("JCLDemo/myapi-impl1.jar");
        assertEquals(gi.morning("Darjeeling"),
                "GOOD MORNING, Darjeeling. I am Firstname Lastname.");
        assertEquals(gi.afternoon("Assam"),
                "GOOD AFTERNOON, Assam. I am Firstname Lastname.");
    }

    @Test
    public void testImpl2() {
        GreetingInterface gi = loadGreetingInterface("JCLDemo/myapi-impl2.jar");
        assertEquals(gi.morning("Darjeeling"),
                "Good Morning, I am Darjeeling. You are Firstname Lastname.");
        assertEquals(gi.afternoon("Assam"),
                "Good Afternoon, I am Assam. You are Firstname Lastname.");
    }

}
