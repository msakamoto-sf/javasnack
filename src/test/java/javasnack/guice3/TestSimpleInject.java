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

import static org.testng.Assert.*;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.google.common.base.Preconditions;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @see http://d.hatena.ne.jp/nodchip/20130126/1359161946
 * @author "Masahiko Sakamoto" <sakamoto.gsyc.3s@gmail.com>
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
        assertEquals(hw.hello(), "Hello Google Guice!!");
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
        assertEquals(hw.hello2(), "Hello Google Guice!!");
    }
}
