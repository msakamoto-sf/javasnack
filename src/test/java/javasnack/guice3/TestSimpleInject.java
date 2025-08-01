/*
 * Copyright 2014 "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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

package javasnack.guice3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author "Masahiko Sakamoto"(msakamoto-sf, sakamoto.gsyc.3s@gmail.com)
 */
/* see:
 * http://d.hatena.ne.jp/nodchip/20130126/1359161946
 */
public class TestSimpleInject {
    public static class HelloWorld {
        String msg;

        public HelloWorld() {
            this.msg = "Hello Google Guice!!";
        }

        public String hello() {
            return this.msg;
        }
    }

    @Test
    public void testSimpleInject() {
        Injector i = Guice.createInjector();
        HelloWorld hw = i.getInstance(HelloWorld.class);
        assertEquals("Hello Google Guice!!", hw.hello());
    }

    public static class HelloWorld2 {
        private final HelloWorld hw;

        @Inject
        public HelloWorld2(HelloWorld hw) {
            this.hw = Preconditions.checkNotNull(hw);
        }

        public String hello2() {
            return hw.hello();
        }
    }

    @Test
    public void testConstructorArgInject() {
        Injector i = Guice.createInjector();
        HelloWorld2 hw = i.getInstance(HelloWorld2.class);
        assertEquals("Hello Google Guice!!", hw.hello2());
    }

    public static class HelloWorld3 {
        private final HelloWorld hw1;

        @Inject
        public HelloWorld3(HelloWorld hw) {
            this.hw1 = Preconditions.checkNotNull(hw);
        }

        public String hello1() {
            return hw1.hello();
        }

        @Inject
        private HelloWorld hw2;

        public String hello2() {
            return hw2.hello();
        }

        private HelloWorld hw3;

        @Inject
        public void setHelloWorld(HelloWorld hw) {
            this.hw3 = hw;
        }

        public String hello3() {
            return hw3.hello();
        }
    }

    @Test
    public void testConstructorAndFieldAndMethodInject() {
        Injector i = Guice.createInjector();
        HelloWorld3 hw = i.getInstance(HelloWorld3.class);
        assertEquals("Hello Google Guice!!", hw.hello1());
        assertEquals("Hello Google Guice!!", hw.hello2());
        assertEquals("Hello Google Guice!!", hw.hello3());
    }

    public static class HelloWorld4 extends HelloWorld3 {

        private final HelloWorld hw4;

        @Inject
        public HelloWorld4(HelloWorld hw, HelloWorld hw4) {
            super(hw);
            this.hw4 = hw4;
        }

        public String hello4() {
            return this.hw4.hello();
        }

        @Inject
        private HelloWorld hw5;

        public String hello5() {
            return this.hw5.hello();
        }
    }

    @Test
    public void testInheritanceInject() {
        Injector i = Guice.createInjector();
        HelloWorld4 hw = i.getInstance(HelloWorld4.class);
        assertEquals("Hello Google Guice!!", hw.hello1());
        assertEquals("Hello Google Guice!!", hw.hello2());
        assertEquals("Hello Google Guice!!", hw.hello3());
        assertEquals("Hello Google Guice!!", hw.hello4());
        assertEquals("Hello Google Guice!!", hw.hello5());
    }
}
