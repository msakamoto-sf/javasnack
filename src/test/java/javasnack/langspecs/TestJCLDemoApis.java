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

package javasnack.langspecs;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;

import org.junit.jupiter.api.Test;
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
public class TestJCLDemoApis {

    GreetingInterface loadGreetingInterface(String jarResourceName) {
        InputStream jar = this.getClass().getClassLoader()
                .getResourceAsStream(jarResourceName);
        JarClassLoader jcl = new JarClassLoader();
        jcl.add(jar);
        JclObjectFactory factory = JclObjectFactory.getInstance();

        // call constructor with arguments.
        Object obj = factory.create(jcl, "myapi.GreetingImpl",
                new String[] { "Firstname", "Lastname" },
                new Class[] { String.class, String.class });

        return JclUtils.cast(obj, GreetingInterface.class);
    }

    @Test
    public void testDefaultImpl() {
        GreetingImpl gi = new GreetingImpl("Firstname", "Lastname");
        assertThat(gi.morning("Java")).isEqualTo(
                "good morning, Java and Firstname Lastname.");
        assertThat(gi.afternoon("Java")).isEqualTo(
                "good afternoon, Java and Firstname Lastname.");
    }

    @Test
    public void testImpl1() {
        GreetingInterface gi = loadGreetingInterface("JCLDemo/myapi-impl1.jar");
        assertThat(gi.morning("Darjeeling")).isEqualTo(
                "GOOD MORNING, Darjeeling. I am Firstname Lastname.");
        assertThat(gi.afternoon("Assam")).isEqualTo(
                "GOOD AFTERNOON, Assam. I am Firstname Lastname.");
    }

    @Test
    public void testImpl2() {
        GreetingInterface gi = loadGreetingInterface("JCLDemo/myapi-impl2.jar");
        assertThat(gi.morning("Darjeeling")).isEqualTo(
                "Good Morning, I am Darjeeling. You are Firstname Lastname.");
        assertThat(gi.afternoon("Assam")).isEqualTo(
                "Good Afternoon, I am Assam. You are Firstname Lastname.");
    }

}
